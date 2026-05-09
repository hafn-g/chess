package com.hafn.chess.network;

import com.hafn.chess.domain.model.*;
import com.hafn.chess.domain.service.CheckRule;
import com.hafn.chess.ui.swing.ChessMultiplayerFrame;
import com.hafn.chess.ui.swing.panel.BoardPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyClient {
    public interface MyClientListener {
        void onError(String message);
        void onGameStart();
    }

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BoardPanel boardPanel;
    private MyClientListener listener;
    private final String nickname;
    private final JFrame parentFrame;
    private final List<String> pendingMessages = new ArrayList<>();

    public MyClient(String host, int port, String nickname, PieceColor color, JFrame parentFrame, MyClientListener listener) throws IOException {
        this.listener = listener;
        this.parentFrame = parentFrame;
        this.nickname = nickname;
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(MessageType.HELLO + " " + nickname + " " + color);
        new Thread(this::listen).start();
    }

    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                final String msg = line;
                SwingUtilities.invokeLater(() -> processMessage(msg));
            }
        } catch (IOException e) {
            if (listener != null) listener.onError("Connection lost");
        }
    }

    private void processMessage(String msg) {
        int firstSpace = msg.indexOf(" ");
        String message;
        if (firstSpace != -1) {
            message = msg.substring(firstSpace + 1).trim();
        } else {
            message = "";
        }

        MessageType type = MessageType.checkMessage(msg);
        if (type != MessageType.GAME_START && type != MessageType.ERROR && boardPanel == null) {
            log.info("BoardPanel not ready, buffering message: {}", msg);
            pendingMessages.add(msg);
            return;
        }

        switch (type) {
            case GAME_START -> {
                onGameStart(PieceColor.fromString(message));
                log.info("Game start from server (my nickname: {} ({}))", nickname, PieceColor.fromString(message));
                if (listener != null) listener.onGameStart();
                break;
            }
            case MOVE -> {
                String from = message.split(" ")[0];
                String to = message.split(" ")[1];

                Cell cellFrom, cellTo;
                try {
                    cellFrom = boardPanel.getState().getCell(from);
                    cellTo = boardPanel.getState().getCell(to);

                    if (boardPanel.getState().getPiece(cellFrom) == null) {
                        log.warn("No piece found in cell {} for MOVE. Ignoring move.", from);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid MOVE: {} -> {}: {}", from, to, e.getMessage());
                    return;
                }

                boardPanel.getBoardController().move(cellFrom, cellTo);
                CheckRule.detectChecks(boardPanel.getState());

                boardPanel.repaint();
                log.info("Move {}", message);
                break;
            }
            case TURN -> {
                boardPanel.getState().setQueue(PieceColor.fromString(message));
                if (PieceColor.fromString(message).equals(boardPanel.getBoardController().getMyMultiplayerColor())) {
                    checkToLocalMove();
                }
                log.info("Turn {}", message);
                break;
            }
            case STATE -> {
                String[] values = message.split(" ");
                boardPanel.getState().setQueue(PieceColor.fromString(values[0]));
                boardPanel.getState().setGameTime(Integer.parseInt(values[1]));
                boardPanel.getState().setWhiteTime(Integer.parseInt(values[2]));
                boardPanel.getState().setBlackTime(Integer.parseInt(values[3]));
                log.info("State {}", message);
                break;
            }
            case ERROR -> {
                if (listener != null) listener.onError(message);
                log.error("Error {}", message);
                break;
            }
        }
    }

    private void checkToLocalMove() {
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final int[] lastSize = {boardPanel.getState().getHistoryMoves().size()};

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<HistoryMove> currentHistory = boardPanel.getState().getHistoryMoves();
                int currentSize = currentHistory.size();

                if (currentSize > lastSize[0]) {
                    out.println(
                            MessageType.MOVE + " " +
                            currentHistory.getLast().getOldCell().getName() + " " +
                            currentHistory.getLast().getNewCell().getName()
                    );
                    scheduler.shutdown();
                }
            } catch (Exception e) {
                log.error("Error from checks local moves", e);
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void onGameStart(PieceColor color) {
        GameConfig config = new GameConfig(GameType.CHESS, 8, 8, 600, PieceColor.WHITE);
        config.setNickname(nickname);
        config.setMyColor(color);
        SwingUtilities.invokeLater(() -> {
            ChessMultiplayerFrame chessFrame = new ChessMultiplayerFrame(config);
            chessFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chessFrame.pack();
            chessFrame.setLocationRelativeTo(null);
            chessFrame.setVisible(true);
            boardPanel = chessFrame.getBoardPanel();
            if (boardPanel != null) {
                boardPanel.repaint();
                boardPanel.revalidate();
            }
            if (parentFrame != null) parentFrame.setVisible(false);
            chessFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (parentFrame != null) parentFrame.setVisible(true);
                }
            });
            if (!pendingMessages.isEmpty()) {
                log.info("Processing pending messages: {}", pendingMessages.size());
                for (String msg : new ArrayList<>(pendingMessages)) {
                    processMessage(msg);
                }
                pendingMessages.clear();
            }
        });
    }
}
