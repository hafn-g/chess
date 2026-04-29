package com.hafn.chess.domain.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;

import static com.hafn.chess.domain.service.MoveHelperGenerator.slidingMoves;

public class Queen extends Piece {
    public Queen(PieceColor color, Cell cell) {
        super(PieceType.QUEEN, color, cell);
    }

    @Override
    public void getNewPossibleMoves(BoardPort state) {
        int[][] dirs = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1},
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        setPossibleMoves(slidingMoves(state, this, dirs));
    }
}
