package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.PieceColor;
import com.hafn.chess.model.PieceType;

import java.util.Set;

import static com.hafn.chess.logic.MoveHelperGenerator.slidingMoves;

public class Bishop extends Piece {
    public Bishop(PieceColor color, Cell cell) {
        super(PieceType.BISHOP, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardState state, Cell cell) {
        int[][] dirs = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };
        return slidingMoves(state, cell, dirs);
    }
}
