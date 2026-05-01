package com.hafn.chess.domain.model;

import com.hafn.chess.domain.piece.Piece;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
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
}
