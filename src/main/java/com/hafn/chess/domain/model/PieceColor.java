package com.hafn.chess.domain.model;

public enum PieceColor {
    WHITE, BLACK;

    public PieceColor next() {
        PieceColor[] values = PieceColor.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public static PieceColor fromString(String str) throws IllegalArgumentException {
        if (str.equalsIgnoreCase("white")) {
            return PieceColor.WHITE;
        } else if (str.equalsIgnoreCase("black")) {
            return PieceColor.BLACK;
        } else {
            throw new IllegalArgumentException("The color was not recognized from the string " + str);
        }
    }
}
