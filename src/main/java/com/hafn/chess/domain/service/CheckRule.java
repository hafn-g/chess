package com.hafn.chess.domain.service;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.state.BoardState;

import java.util.Map;

public abstract class CheckRule {

    public static void detectChecks(BoardPort state) {
        detectChecks(state, state.getPieces());
    }

    public static void detectChecks(BoardPort state, Map<Cell, Piece> pieceMap) {
        var ref = new Object() {
            Cell blackKing = null;
            Cell whiteKing = null;
            boolean blackShah = false;
            boolean whiteShah = false;
        };

        pieceMap.forEach((cell, piece) -> {
            if (piece.getType().equals(PieceType.KING)) {
                if (piece.getColor().equals(PieceColor.BLACK)) {
                    ref.blackKing = cell;
                } else {
                    ref.whiteKing = cell;
                }
            }
        });

        pieceMap.forEach((_, piece) -> {
            piece.getNewPossibleMoves(state);

            if (piece.getPossibleMoves().contains(ref.blackKing)) {
                ref.blackShah = true;
            }

            if (piece.getPossibleMoves().contains(ref.whiteKing)) {
                ref.whiteShah = true;
            }
        });

        state.setBlackShah(ref.blackShah);
        state.setWhiteShah(ref.whiteShah);
    }

    public static void generateAllPossibleMoves(BoardState state) {
        state.getPieces().forEach((_, piece) -> {
            piece.getNewPossibleMoves(state);
        });
    }
}
