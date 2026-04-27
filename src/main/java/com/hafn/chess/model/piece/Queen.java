package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.PieceColor;
import com.hafn.chess.model.PieceType;

import java.util.Set;

import static com.hafn.chess.logic.MoveHelperGenerator.slidingMoves;

public class Queen extends Piece {
    public Queen(PieceColor color, Cell cell) {
        super(PieceType.QUEEN, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardState state, Cell cell) {
        int[][] dirs = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1},
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };
        return slidingMoves(state, cell, dirs);
    }
}
