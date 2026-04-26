package com.hafn.logic;

import com.hafn.data.Piece;
import com.hafn.data.PieceColor;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MoveGenerator {

    public static Set<Point> generate(BoardState state, int row, int col) {
        Piece piece = state.getPiece(row, col);
        if (piece == null) return Set.of();

        return switch (piece.getType()) {
            case KING -> kingMoves(state, row, col);
            case ROOK -> rookMoves(state, row, col);
            case BISHOP -> bishopMoves(state, row, col);
            case KNIGHT -> knightMoves(state, row, col);
            case QUEEN -> queenMoves(state, row, col);
            case PAWN -> pawnMoves(state, row, col);
            default -> pawnMoves(state, row, col);
        };
    }

    private static Set<Point> kingMoves(BoardState state, int row, int col) {
        Set<Point> moves = new HashSet<>();
        Piece currentPiece = state.getPiece(row, col);

        int[] d = {-1, 0, 1};

        for (int dr : d) {
            for (int dc : d) {
                if (dr == 0 && dc == 0) continue;

                int r = row + dr;
                int c = col + dc;

                if (!inBounds(state, r, c)) continue;

                Piece targetPiece = state.getPiece(r, c);
                if (targetPiece == null ||
                        targetPiece.getColor() != currentPiece.getColor()) {
                    moves.add(new Point(r, c));
                }
            }
        }

        return moves;
    }

    private static Set<Point> knightMoves(BoardState state, int row, int col) {
        int[][] dirs = {
                {-2, -1}, {-1, -2}, {2, 1}, {1, 2},
                {2, -1}, {1, -2}, {-2, 1}, {-1, 2}
        };
        return slidingMoves(state, row, col, dirs);
    }

    private static Set<Point> bishopMoves(BoardState state, int row, int col) {
        int[][] dirs = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };
        return slidingMoves(state, row, col, dirs);
    }

    private static Set<Point> rookMoves(BoardState state, int row, int col) {
        int[][] dirs = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };
        return slidingMoves(state, row, col, dirs);
    }

    private static Set<Point> queenMoves(BoardState state, int row, int col) {
        int[][] dirs = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1},
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };
        return slidingMoves(state, row, col, dirs);
    }

    private static Set<Point> pawnMoves(BoardState state, int row, int col) {
        Set<Point> moves = new HashSet<>();
        Piece current = state.getPiece(row, col);

        int dir = (current.getColor() == PieceColor.WHITE) ? -1 : 1;

        int forwardRow = row + dir;

        // forward move
        if (inBounds(state, forwardRow, col)) {
            if (state.getPiece(forwardRow, col) == null) {
                moves.add(new Point(forwardRow, col));
            }
        }

        // captures
        int[] dc = {-1, 1};
        for (int d : dc) {
            int c = col + d;

            if (!inBounds(state, forwardRow, c)) continue;

            Piece target = state.getPiece(forwardRow, c);
            if (target != null && target.getColor() != current.getColor()) {
                moves.add(new Point(forwardRow, c));
            }
        }

        return moves;
    }

    private static Set<Point> slidingMoves(BoardState state, int row, int col, int[][] directions) {
        Set<Point> moves = new HashSet<>();
        Piece current = state.getPiece(row, col);

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            while (inBounds(state, r, c)) {
                Piece target = state.getPiece(r, c);

                if (target == null) {
                    moves.add(new Point(r, c));
                } else {
                    if (target.getColor() != current.getColor()) {
                        moves.add(new Point(r, c)); // capture
                    }
                    break; // stop after collision
                }

                r += dir[0];
                c += dir[1];
            }
        }

        return moves;
    }

    private static boolean inBounds(BoardState state, int r, int c) {
        return r >= 0 && r < state.rows() &&
                c >= 0 && c < state.cols();
    }
}