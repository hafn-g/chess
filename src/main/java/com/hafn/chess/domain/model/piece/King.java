package com.hafn.chess.domain.model.piece;

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
    public Set<Cell> possibleMoves(BoardPort state, Cell cell) {
        Set<Cell> moves = new HashSet<>();
        Piece currentPiece = state.getPiece(cell);

        int[] d = {-1, 0, 1};

        for (int dr : d) {
            for (int dc : d) {
                if (dr == 0 && dc == 0) continue;

                int r = cell.getRow() + dr;
                int c = cell.getCol() + dc;

                if (!state.inBounds(r, c)) continue;
                Cell targetCell = state.getCell(r, c);

                Piece targetPiece = state.getPiece(targetCell);
                if (targetPiece == null ||
                        targetPiece.getColor() != currentPiece.getColor()) {
                    moves.add(targetCell);
                }
            }
        }

        return moves;
    }
}
