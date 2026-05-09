package com.hafn.chess.network;

public enum MessageType {
    HELLO, STATE,
    GAME_START, GAME_END, MOVE, TURN, ERROR;

    public static MessageType checkMessage(String message) {
        try {
            return MessageType.valueOf(message.trim().split(" ")[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Message type not recognized", e);
        }
    }
}
