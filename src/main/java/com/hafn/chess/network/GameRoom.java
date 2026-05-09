package com.hafn.chess.network;

import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.GameConfig;
import com.hafn.chess.domain.model.GameType;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.state.BoardState;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameRoom {
    private final int id;
    private final Map<PieceColor, ClientHandler> handlers;
    private final NetworkGame game;
    private ClientHandler currentTurn;

    public GameRoom(int id, Map<PieceColor, ClientHandler> handlers) {
        this.id = id;
        this.handlers = Map.copyOf(handlers);

        GameConfig config = new GameConfig(GameType.CHESS, 8, 8, 600, PieceColor.WHITE);
        BoardState state = BoardInitializer.createDefaultState(config);
        this.game = new NetworkGame(state);
    }

    public void startGame() {
        this.currentTurn = handlers.get(PieceColor.WHITE);

        handlers.forEach((_, handler) -> {
            handler.sendMessage(MessageType.GAME_START, handler.getPlayer().getColor().toString());
            handler.sendMessage(MessageType.TURN, currentTurn.getPlayer().getColor().toString());

            handler.setRoom(this);
        });

        log.info("Game starting");
        checkToLocalMove();
    }

    public synchronized void handlePlayerMessage(ClientHandler sender, String message) {
        if (sender != currentTurn) {
            sender.sendMessage(MessageType.ERROR, "It's not your move");
            return;
        }

        log.info("Received message from {}: {}", sender.getPlayer().getNickname(), message);

        switch (MessageType.checkMessage(message)) {
            case MOVE -> {
                String[] parts = message.substring(5).split(" ");
                if (parts.length != 2) {
                    sender.sendMessage(MessageType.ERROR, "Invalid move format");
                    return;
                }
                String from = parts[0];
                String to = parts[1];

                try {
                    game.getController().move(from, to);

                    sendMessageToAllPlayersWithoutSender(sender, MessageType.MOVE, from + " " + to);
                    sendMessageToAllPlayers(MessageType.TURN, game.getState().getQueue().toString());
                    currentTurn = handlers.get(game.getState().getQueue());
                } catch (MoveException | IllegalArgumentException e) {
                    sender.sendMessage(MessageType.ERROR, e.getMessage());
                }
                break;
            }
            default -> {
                sender.sendMessage(MessageType.ERROR, "Invalid message type");
                break;
            }
        }
    }

    public void playerDisconnected(ClientHandler player) {
        if (handlers.size() == 2) {
            handlers.forEach((_, handler) -> {
                handler.sendMessage(MessageType.GAME_END, "lose " + player.getPlayer().getNickname() + " (player disconnected)");
                handler.setRoom(null);
            });
        }
    }

    private void sendMessageToAllPlayers(MessageType messageType, String msg) {
        handlers.forEach((_, handler) -> handler.sendMessage(messageType, msg));
    }

    private void sendMessageToAllPlayersWithoutSender(ClientHandler sender, MessageType messageType, String msg) {
        handlers.forEach((_, handler) -> {
            if (!handler.equals(sender)) handler.sendMessage(messageType, msg);
        });
    }

    private void sendMessageToAllPlayers(MessageType messageType) {
        handlers.forEach((_, handler) -> {
            handler.sendMessage(messageType);
        });
    }

    private void checkToLocalMove() {
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {

                String message = game.getState().getQueue() +
                        " " +
                        game.getState().getGameTime() +
                        " " +
                        game.getState().getWhiteTime() +
                        " " +
                        game.getState().getBlackTime();

                sendMessageToAllPlayers(MessageType.STATE, message);

                if (game.getState().isPauseGame()) {
                    sendMessageToAllPlayers(MessageType.GAME_END);
                    scheduler.shutdown();
                }
            } catch (Exception e) {
                log.error("Error from checks local moves", e);
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
