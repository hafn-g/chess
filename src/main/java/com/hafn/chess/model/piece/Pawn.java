package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.PieceColor;
import com.hafn.chess.model.PieceType;

import java.util.HashSet;
import java.util.Set;

import static com.hafn.chess.logic.MoveHelperGenerator.inBounds;

public class Pawn extends Piece {
    public Pawn(PieceColor color, Cell cell) {
        super(PieceType.PAWN, color, cell);
    }

    @Override
    public Set<Cell> possibleMoves(BoardState state, Cell cell) {
        Set<Cell> moves = new HashSet<>();
        Piece current = state.getPiece(cell);

        int dir = (current.getColor() == PieceColor.WHITE) ? -1 : 1;

        int forwardRow = cell.getRow() + dir;

        // forward move
        if (inBounds(state, forwardRow, cell.getCol())) {
            Cell targetCell = state.getCell(forwardRow, cell.getCol());
            if (state.getPiece(targetCell) == null) {
                moves.add(targetCell);
            }
        }

        // captures
        int[] dc = {-1, 1};
        for (int d : dc) {
            int c = cell.getCol() + d;

            if (!inBounds(state, forwardRow, c)) continue;

            Cell targetCell = state.getCell(forwardRow, c);
            Piece target = state.getPiece(targetCell);
            if (target != null && target.getColor() != current.getColor()) {
                moves.add(targetCell);
            }
        }

        return moves;
    }
}
