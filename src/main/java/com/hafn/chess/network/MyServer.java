package com.hafn.chess.network;

import com.hafn.chess.domain.model.PieceColor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MyServer {
    @Getter
    private final Map<PieceColor, ClientHandler> waitingPlayers = new HashMap<>();
    private final int roomId = 1;

    public void start(final int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            log.info("The server was started on port: {}", portNumber);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            log.error("Server error", e);
        }
    }

    public void startGame() throws RuntimeException {
        if (waitingPlayers.size() >= 2) {
            GameRoom room = new GameRoom(roomId, waitingPlayers);
            room.startGame();
        } else {
            log.error("Minimum two players");
            throw new RuntimeException("Minimum two players");
        }
    }

    public void addWaitingPlayer(ClientHandler handler) throws IllegalArgumentException {
        if (this.waitingPlayers.containsKey(handler.getPlayer().getColor())) {
            throw new IllegalArgumentException("The color is taken");
        } else {
            this.waitingPlayers.put(handler.getPlayer().getColor(), handler);
        }
    }
}
