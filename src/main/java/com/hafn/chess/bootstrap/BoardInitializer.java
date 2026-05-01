package com.hafn.chess.bootstrap;

import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.*;
import com.hafn.chess.domain.state.BoardState;

import static com.hafn.chess.domain.service.CheckRule.checkAllPiece;

public abstract class BoardInitializer {

    public static BoardState createDefaultState(PieceColor queue) {
        BoardState state = new BoardState(8, 8, queue);
        initCells(state);
        initPieces(state);
        checkAllPiece(state);
        return state;
    }

    public static void initCells(BoardState state) {
        for (int r = 0; r < state.getRows(); r++) {
            for (int c = 0; c < state.getCols(); c++) {
                String cellName = "" + (char) ('a' + c) + (state.getRows() - r);
                state.addCell(r, c, cellName.toUpperCase());
            }
        }
    }

    public static void initPieces(BoardState state) {
        // pawns
        for (int c = 0; c < state.getCols(); c++) {
            state.addPiece(new Pawn(PieceColor.BLACK, state.getCell(1, c)));
            state.addPiece(new Pawn(PieceColor.WHITE, state.getCell(6, c)));
        }

        // rooks
        state.addPiece(new Rook(PieceColor.BLACK, state.getCell(0, 0)));
        state.addPiece(new Rook(PieceColor.BLACK, state.getCell(0, 7)));
        state.addPiece(new Rook(PieceColor.WHITE, state.getCell(7, 0)));
        state.addPiece(new Rook(PieceColor.WHITE, state.getCell(7, 7)));

        // knights
        state.addPiece(new Knight(PieceColor.BLACK, state.getCell(0, 1)));
        state.addPiece(new Knight(PieceColor.BLACK, state.getCell(0, 6)));
        state.addPiece(new Knight(PieceColor.WHITE, state.getCell(7, 1)));
        state.addPiece(new Knight(PieceColor.WHITE, state.getCell(7, 6)));

        // bishops
        state.addPiece(new Bishop(PieceColor.BLACK, state.getCell(0, 2)));
        state.addPiece(new Bishop(PieceColor.BLACK, state.getCell(0, 5)));
        state.addPiece(new Bishop(PieceColor.WHITE, state.getCell(7, 2)));
        state.addPiece(new Bishop(PieceColor.WHITE, state.getCell(7, 5)));

        // queens
        state.addPiece(new Queen(PieceColor.BLACK, state.getCell(0, 3)));
        state.addPiece(new Queen(PieceColor.WHITE, state.getCell(7, 3)));

        // kings
        state.addPiece(new King(PieceColor.BLACK, state.getCell(0, 4)));
        state.addPiece(new King(PieceColor.WHITE, state.getCell(7, 4)));
    }
}
