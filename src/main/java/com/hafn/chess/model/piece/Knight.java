package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.PieceColor;
import com.hafn.chess.model.PieceType;

import java.util.HashSet;
import java.util.Set;

public class Knight extends Piece {
    public Knight(PieceColor color, Cell cell) {
        super(PieceType.KNIGHT, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardState state, Cell cell) {
        Set<Cell> moves = new HashSet<>();
        int[][] dirs = {
                {-2, -1}, {-1, -2}, {2, 1}, {1, 2},
                {2, -1}, {1, -2}, {-2, 1}, {-1, 2}
        };
        for (int[] dir : dirs) {
            int r = cell.getRow() + dir[0];
            int c = cell.getCol() + dir[1];
            if (r >= 0 && r < state.rows() && c >= 0 && c < state.cols()) {
                Cell target = state.getCell(r, c);
                Piece targetPiece = state.getPiece(target);
                if (targetPiece == null || targetPiece.getColor() != getColor()) {
                    moves.add(target);
                }
            }
        }
        return moves;
    }
}
