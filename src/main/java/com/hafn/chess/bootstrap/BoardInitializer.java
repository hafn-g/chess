package com.hafn.chess.bootstrap;

import com.hafn.chess.domain.model.GameConfig;
import com.hafn.chess.domain.model.GameType;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.*;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.service.CheckRule;
import com.hafn.chess.domain.state.BoardState;

public abstract class BoardInitializer {

    public static BoardState createDefaultState(GameConfig config) {
        BoardState state = new BoardState(config.getRows(), config.getCols(), config.getPlayerTime(), config.getQueue());
        initCells(state);
        if (config.getGameType().equals(GameType.CHESS)) {
            initChessPieces(state);
        } else if (config.getGameType().equals(GameType.CHECKERS)) {
            initCheckerPieces(state);
        }
        CheckRule.detectChecks(state);
        return state;
    }

    public static void initCells(BoardPort state) {
        for (int r = 0; r < state.getRows(); r++) {
            for (int c = 0; c < state.getCols(); c++) {
                String cellName = "" + (char) ('a' + c) + (state.getRows() - r);
                state.addCell(r, c, cellName.toUpperCase());
            }
        }
    }

    public static void initCheckerPieces(BoardPort state) {
        for (int i = 0; i < 3; i++) {
            for (int c = 0; c < state.getCols(); c++) {
                if (!state.getCell(i, c).isLight()) {
                    state.addPiece(new Checker(PieceColor.BLACK, state.getCell(i, c)));
                }

                if (!state.getCell(state.getRows() - 1 - i, c).isLight()) {
                    state.addPiece(new Checker(PieceColor.WHITE, state.getCell(state.getRows() - 1 - i, c)));
                }
            }
        }
    }

    public static void initChessPieces(BoardPort state) {
        // pawns
        for (int c = 0; c < state.getCols(); c++) {
            state.addPiece(new Pawn(PieceColor.BLACK, state.getCell(1, c)));
            state.addPiece(new Pawn(PieceColor.WHITE, state.getCell(state.getRows()-2, c)));
        }

        if (state.getCols() > 8) {
            for (int c = 8; c < state.getCols(); c++) {
                state.addPiece(new Pawn(PieceColor.BLACK, state.getCell(0, c)));
                state.addPiece(new Pawn(PieceColor.WHITE, state.getCell(state.getRows()-1, c)));
            }
        }

        // rooks
        state.addPiece(new Rook(PieceColor.BLACK, state.getCell(0, 0)));
        state.addPiece(new Rook(PieceColor.BLACK, state.getCell(0, 7)));
        state.addPiece(new Rook(PieceColor.WHITE, state.getCell(state.getRows()-1, 0)));
        state.addPiece(new Rook(PieceColor.WHITE, state.getCell(state.getRows()-1, 7)));

        // knights
        state.addPiece(new Knight(PieceColor.BLACK, state.getCell(0, 1)));
        state.addPiece(new Knight(PieceColor.BLACK, state.getCell(0, 6)));
        state.addPiece(new Knight(PieceColor.WHITE, state.getCell(state.getRows()-1, 1)));
        state.addPiece(new Knight(PieceColor.WHITE, state.getCell(state.getRows()-1, 6)));

        // bishops
        state.addPiece(new Bishop(PieceColor.BLACK, state.getCell(0, 2)));
        state.addPiece(new Bishop(PieceColor.BLACK, state.getCell(0, 5)));
        state.addPiece(new Bishop(PieceColor.WHITE, state.getCell(state.getRows()-1, 2)));
        state.addPiece(new Bishop(PieceColor.WHITE, state.getCell(state.getRows()-1, 5)));

        // queens
        state.addPiece(new Queen(PieceColor.BLACK, state.getCell(0, 3)));
        state.addPiece(new Queen(PieceColor.WHITE, state.getCell(state.getRows()-1, 3)));

        // kings
        state.addPiece(new King(PieceColor.BLACK, state.getCell(0, 4)));
        state.addPiece(new King(PieceColor.WHITE, state.getCell(state.getRows()-1, 4)));
    }
}
