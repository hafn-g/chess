package com.hafn.chess.domain.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.HistoryMove;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode
public abstract class Piece implements Move {
    private final PieceType type;
    private final PieceColor color;

    @Setter
    private Set<Cell> possibleMoves;
    @Setter
    private Cell cell;

    public Piece(PieceType type, PieceColor color, Cell cell) {
        this.type = type;
        this.color = color;
        this.cell = cell;
        this.possibleMoves = new HashSet<>();
    }

    @Override
    public void execute(BoardPort state, Cell clicked) {
        Cell oldCel = state.getClickedCell();
        Piece clickedCellPiece = state.getPiece(clicked);

        if (clickedCellPiece != null) {
            if (clickedCellPiece.getType().equals(PieceType.KING)) {
                return;
            }
        }

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
    public void undo(BoardPort state) {
        HistoryMove lastMove = state.getHistoryMoves().getLast();
        if (lastMove == null) return;

        Cell from = lastMove.getOldCell();
        Cell to = lastMove.getNewCell();
        Piece capturedPiece = lastMove.getPieceDestroyed();

        state.removePiece(to);
        this.setCell(from);
        state.addPiece(this);

        if (capturedPiece != null) {
            state.addPiece(capturedPiece);
        }

        state.setClickedCell(null);
    }

    @Override
    public String toString() {
        return "Piece{" +
                "cell=" + cell +
                ", color=" + color +
                ", type=" + type +
                '}';
    }
}