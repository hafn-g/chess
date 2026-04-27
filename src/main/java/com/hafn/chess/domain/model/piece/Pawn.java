package com.hafn.chess.domain.model.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    public Pawn(PieceColor color, Cell cell) {
        super(PieceType.PAWN, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardPort state, Cell cell) {
        Set<Cell> moves = new HashSet<>();
        Piece current = state.getPiece(cell);

        int dir = (current.getColor() == PieceColor.WHITE) ? -1 : 1;

        int forwardRow = cell.getRow() + dir;

        // forward move
        if (state.inBounds(forwardRow, cell.getCol())) {
            Cell targetCell = state.getCell(forwardRow, cell.getCol());
            if (state.getPiece(targetCell) == null) {
                moves.add(targetCell);
            }
        }

        // captures
        int[] dc = {-1, 1};
        for (int d : dc) {
            int c = cell.getCol() + d;

            if (!state.inBounds(forwardRow, c)) continue;

            Cell targetCell = state.getCell(forwardRow, c);
            Piece target = state.getPiece(targetCell);
            if (target != null && target.getColor() != current.getColor()) {
                moves.add(targetCell);
            }
        }

        return moves;
    }
}
