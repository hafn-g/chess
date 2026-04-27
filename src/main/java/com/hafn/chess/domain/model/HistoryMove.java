package com.hafn.chess.domain.model;

import com.hafn.chess.domain.model.piece.Piece;

import java.util.Objects;

public class HistoryMove {
    private final Piece piece;
    private final Cell oldCell;
    private final Cell newCell;
    private final Piece pieceDestroyed;

    public HistoryMove(Piece piece, Cell oldCell, Cell newCell, Piece pieceDestroyed) {
        if (piece == null || oldCell == null || newCell == null) {
            throw new NullPointerException();
        }

        this.piece = piece;
        this.oldCell = oldCell;
        this.newCell = newCell;
        this.pieceDestroyed = pieceDestroyed;
    }

    public Cell getNewCell() {
        return newCell;
    }

    public Cell getOldCell() {
        return oldCell;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getPieceDestroyed() {
        return pieceDestroyed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistoryMove that = (HistoryMove) o;
        return Objects.equals(piece, that.piece) && Objects.equals(oldCell, that.oldCell) &&
                Objects.equals(newCell, that.newCell) && Objects.equals(pieceDestroyed, that.pieceDestroyed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, oldCell, newCell, pieceDestroyed);
    }

    @Override
    public String toString() {
        return "HistoryMove{" +
                "newCell=" + newCell +
                ", piece=" + piece +
                ", oldCell=" + oldCell +
                ", pieceDestroyed=" + pieceDestroyed +
                '}';
    }
}
