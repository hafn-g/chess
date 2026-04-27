package com.hafn.chess.domain.model.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;

import java.util.HashSet;
import java.util.Set;

public class Knight extends Piece {
    public Knight(PieceColor color, Cell cell) {
        super(PieceType.KNIGHT, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardPort state, Cell cell) {
        Set<Cell> moves = new HashSet<>();
        int[][] dirs = {
                {-2, -1}, {-1, -2}, {2, 1}, {1, 2},
                {2, -1}, {1, -2}, {-2, 1}, {-1, 2}
        };
        for (int[] dir : dirs) {
            int r = cell.getRow() + dir[0];
            int c = cell.getCol() + dir[1];
            if (!state.inBounds(r, c)) continue;
            Cell target = state.getCell(r, c);
            Piece targetPiece = state.getPiece(target);
            if (targetPiece == null || targetPiece.getColor() != getColor()) {
                moves.add(target);
            }
        }
        return moves;
    }
}
