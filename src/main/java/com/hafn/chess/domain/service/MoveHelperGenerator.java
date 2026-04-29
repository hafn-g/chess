package com.hafn.chess.domain.service;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.port.BoardPort;

import java.util.HashSet;
import java.util.Set;

public class MoveHelperGenerator {

    public static Set<Cell> slidingMoves(BoardPort state, Piece piece, int[][] directions) {
        Set<Cell> moves = new HashSet<>();

        for (int[] dir : directions) {
            int r = piece.getCell().getRow() + dir[0];
            int c = piece.getCell().getCol() + dir[1];

            while (state.inBounds(r, c)) {
                Cell targetCell = state.getCell(r, c);
                Piece target = state.getPiece(targetCell);

                if (target == null) {
                    moves.add(targetCell);
                } else {
                    if (target.getColor() != piece.getColor()) {
                        moves.add(targetCell); // capture
                    }
                    break; // stop after collision
                }

                r += dir[0];
                c += dir[1];
            }
        }

        return moves;
    }
}