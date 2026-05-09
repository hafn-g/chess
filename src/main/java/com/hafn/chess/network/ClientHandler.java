package com.hafn.chess.network;

import com.hafn.chess.domain.model.PieceColor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@EqualsAndHashCode
@ToString
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final MyServer server;
    private PrintWriter out;
    private BufferedReader in;

    @Setter
    private GameRoom room;

    @Getter
    private Player player;

    public ClientHandler(Socket socket, MyServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String firstLine = in.readLine();
            if (firstLine == null || !firstLine.startsWith("HELLO ")) {
                sendMessage(MessageType.ERROR, "Expected HELLO <nickname> <color>");
                return;
            }
            Pattern pattern = Pattern.compile("HELLO (\\w+) (\\w+)");
            Matcher matcher = pattern.matcher(firstLine);

            if (!matcher.find()) {
                sendMessage(MessageType.ERROR, "Inappropriate data sent, needed HELLO <nickname> <color>");
                return;
            }
            String nickname = matcher.group(1);
            String strColor = matcher.group(2);
            PieceColor color;
            try {
                color = PieceColor.fromString(strColor);
            } catch (IllegalArgumentException e) {
                sendMessage(MessageType.ERROR, "Color was not recognized, please HELLO <nickname> <color>");
                return;
            }

            player = new Player(nickname, color);

            try {
                server.addWaitingPlayer(this);
            } catch (IllegalArgumentException e) {
                sendMessage(MessageType.ERROR, "The color is taken");
                return;
            }

            log.info("Player {} has joined", player);

            String line;
            while ((line = in.readLine()) != null) {
                if (room != null) {
                    room.handlePlayerMessage(this, line);
                } else {
                    sendMessage(MessageType.ERROR, "The game hasn't been scheduled yet, please wait...");
                }
            }
        } catch (IOException e) {
            log.error("Lost connection with {}", player, e);
        } finally {
            if (room != null) room.playerDisconnected(this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void sendMessage(MessageType messageType, String msg) {
        log.info("Message sent to {}: {} {}", player.getNickname(), messageType.toString().toUpperCase(), msg);
        out.println(messageType + " " + msg);
    }

    public void sendMessage(MessageType messageType) {
        log.info("Message sent to {}: {} (only MessageType)", player.getNickname(), messageType.toString());
        out.println(messageType);
    }
}
