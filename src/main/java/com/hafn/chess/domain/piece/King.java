package com.hafn.chess.domain.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;

import java.util.HashSet;
import java.util.Set;

public class King extends Piece {
    public King(PieceColor color, Cell cell) {
        super(PieceType.KING, color, cell);
    }

    @Override
    public void getNewPossibleMoves(BoardPort state) {
        Set<Cell> moves = new HashSet<>();

        int[] d = {-1, 0, 1};

        for (int dr : d) {
            for (int dc : d) {
                if (dr == 0 && dc == 0) continue;

                int r = this.getCell().getRow() + dr;
                int c = this.getCell().getCol() + dc;

                if (!state.inBounds(r, c)) continue;
                Cell targetCell = state.getCell(r, c);

                Piece targetPiece = state.getPiece(targetCell);
                if (targetPiece == null ||
                        targetPiece.getColor() != this.getColor()) {
                    moves.add(targetCell);
                }
            }
        }

        setPossibleMoves(moves);
    }
}

