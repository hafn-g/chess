package com.hafn.chess.domain.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    @Getter
    private boolean isMoved = false;
    private boolean movedInLast = false;

    public Pawn(PieceColor color, Cell cell) {
        super(PieceType.PAWN, color, cell);
    }

    @Override
    public void getNewPossibleMoves(BoardPort state) {
        Set<Cell> moves = new HashSet<>();

        int dir = (this.getColor() == PieceColor.WHITE) ? -1 : 1;
        int startRow = this.getCell().getRow();
        int forwardRow = startRow + dir;

        // forward move
        if (state.inBounds(forwardRow, this.getCell().getCol())) {
            Cell targetCell = state.getCell(forwardRow, this.getCell().getCol());
            if (state.getPiece(targetCell) == null) {
                moves.add(targetCell);
                if (!isMoved) {
                    int doubleForwardRow = startRow + 2 * dir;
                    if (state.inBounds(doubleForwardRow, this.getCell().getCol())) {
                        Cell doubleTargetCell = state.getCell(doubleForwardRow, this.getCell().getCol());
                        if (state.getPiece(doubleTargetCell) == null) {
                            moves.add(doubleTargetCell);
                        }
                    }
                }
            }
        }

        // captures
        int[] dc = {-1, 1};
        for (int d : dc) {
            int c = this.getCell().getCol() + d;

            if (!state.inBounds(forwardRow, c)) continue;

            Cell targetCell = state.getCell(forwardRow, c);
            Piece target = state.getPiece(targetCell);
            if (target != null && target.getColor() != this.getColor()) {
                moves.add(targetCell);
            }
        }

        setPossibleMoves(moves);
    }

    @Override
    public void execute(BoardPort state, Cell clicked) {
        super.execute(state, clicked);
        if (!isMoved) {
            isMoved = true;
            movedInLast = true;
        } else {
            movedInLast = false;
        }
    }

    @Override
    public void undo(BoardPort state) {
        super.undo(state);
        if (movedInLast) {
            isMoved = false;
            movedInLast = false;
        }
    }
}
