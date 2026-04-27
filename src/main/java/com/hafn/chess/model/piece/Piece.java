package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.HistoryMove;
import com.hafn.chess.model.PieceColor;
import com.hafn.chess.model.PieceType;
import com.hafn.chess.ui.ImageCache;

import java.awt.*;
import java.util.Objects;

public abstract class Piece implements Move {
    private final PieceType type;
    private final PieceColor color;
    private final Image image;

    private Cell cell;

    public Piece(PieceType type, PieceColor color, Cell cell) {
        this.type = type;
        this.color = color;
        this.cell = cell;
        this.image = loadImage();
    }

    private Image loadImage() {
        String key = this.getColor().name().toLowerCase() + "_" +
                this.getType().name().toLowerCase();
        return ImageCache.get(key);
    }

    public Image getImage() {
        return image;
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

    @Override
    public void execute(BoardState state, Cell clicked) {
        Cell oldCel = state.getClickedCell();
        Piece clickedCellPiece = state.getPiece(clicked);

        HistoryMove historyMove = new HistoryMove(this, oldCel, clicked, clickedCellPiece);
        state.addHistoryMoves(historyMove);
        state.setClickedCell(null);

        if (clickedCellPiece != null) {
            state.removePiece(clicked);
        }
        this.setCell(clicked);
        state.removePiece(oldCel);
        state.addPiece(this);
    }

    @Override
    public void undo(BoardState state) {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return type == piece.type && color == piece.color && Objects.equals(cell, piece.cell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, cell);
    }

    @Override
    public String toString() {
        return "Piece{" +
                "cell=" + cell +
                ", type=" + type +
                ", color=" + color +
                '}';
    }
}