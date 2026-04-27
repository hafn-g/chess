package com.hafn.chess.domain.model.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;

import java.util.Set;

import static com.hafn.chess.domain.service.MoveHelperGenerator.slidingMoves;

public class Bishop extends Piece {
    public Bishop(PieceColor color, Cell cell) {
        super(PieceType.BISHOP, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardPort state, Cell cell) {
        int[][] dirs = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };
        return slidingMoves(state, cell, dirs);
    }
}
