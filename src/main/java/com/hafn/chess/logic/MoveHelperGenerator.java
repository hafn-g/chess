package com.hafn.chess.logic;

import com.hafn.chess.model.Cell;
import com.hafn.chess.model.piece.Piece;

import java.util.HashSet;
import java.util.Set;

public class MoveHelperGenerator {

    public static Set<Cell> slidingMoves(BoardState state, Cell cell, int[][] directions) {
        Set<Cell> moves = new HashSet<>();
        Piece current = state.getPiece(cell);

        for (int[] dir : directions) {
            int r = cell.getRow() + dir[0];
            int c = cell.getCol() + dir[1];

            while (state.inBounds(r, c)) {
                Cell targetCell = state.getCell(r, c);
                Piece target = state.getPiece(targetCell);

                if (target == null) {
                    moves.add(targetCell);
                } else {
                    if (target.getColor() != current.getColor()) {
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

//    public static boolean inBounds(BoardState state, int r, int c) {
//        return r >= 0 && r < state.getRows() &&
//                c >= 0 && c < state.getCols();
//    }
}