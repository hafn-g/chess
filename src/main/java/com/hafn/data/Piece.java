package com.hafn.data;

public class Piece {
    private final PieceType type;
    private final PieceColor color;

    private Cell cell;

    public Piece(PieceType type, PieceColor color, Cell cell) {
        this.type = type;
        this.color = color;
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}