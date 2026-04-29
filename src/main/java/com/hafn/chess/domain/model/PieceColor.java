package com.hafn.chess.domain.model;

public enum PieceColor {
    WHITE, BLACK;

    public PieceColor next() {
        PieceColor[] values = PieceColor.values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
