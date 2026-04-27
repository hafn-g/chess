package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.PieceColor;
import com.hafn.chess.model.PieceType;

import java.util.HashSet;
import java.util.Set;

import static com.hafn.chess.logic.MoveHelperGenerator.inBounds;

public class King extends Piece {
    public King(PieceColor color, Cell cell) {
        super(PieceType.KING, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardState state, Cell cell) {
        Set<Cell> moves = new HashSet<>();
        Piece currentPiece = state.getPiece(cell);

        int[] d = {-1, 0, 1};

        for (int dr : d) {
            for (int dc : d) {
                if (dr == 0 && dc == 0) continue;

                int r = cell.getRow() + dr;
                int c = cell.getCol() + dc;

                if (!inBounds(state, r, c)) continue;
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
