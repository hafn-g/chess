package com.hafn.logic;

import com.hafn.data.Piece;
import com.hafn.data.PieceColor;
import com.hafn.data.PieceType;

public class BoardInitializer {

    public static void initCells(BoardState state) {
        for (int r = 0; r < state.rows(); r++) {
            for (int c = 0; c < state.cols(); c++) {
                state.addCell(r, c);
            }
        }
    }

    public static void initPieces(BoardState state) {
        // test
        state.setPiece(new Piece(PieceType.KNIGHT, PieceColor.WHITE, state.getCell(2, 5)));
        state.setPiece(new Piece(PieceType.BISHOP, PieceColor.WHITE, state.getCell(2, 0)));
        state.setPiece(new Piece(PieceType.QUEEN, PieceColor.WHITE, state.getCell(3, 2)));
        state.setPiece(new Piece(PieceType.ROOK, PieceColor.WHITE, state.getCell(3, 3)));

        // pawns
        for (int c = 0; c < state.cols(); c++) {
            state.setPiece(new Piece(PieceType.PAWN, PieceColor.BLACK, state.getCell(1, c)));
            state.setPiece(new Piece(PieceType.PAWN, PieceColor.WHITE, state.getCell(6, c)));
        }

        // rooks
        state.setPiece(new Piece(PieceType.ROOK, PieceColor.BLACK, state.getCell(0, 0)));
        state.setPiece(new Piece(PieceType.ROOK, PieceColor.BLACK, state.getCell(0, 7)));
        state.setPiece(new Piece(PieceType.ROOK, PieceColor.WHITE, state.getCell(7, 0)));
        state.setPiece(new Piece(PieceType.ROOK, PieceColor.WHITE, state.getCell(7, 7)));

        // knights
        state.setPiece(new Piece(PieceType.KNIGHT, PieceColor.BLACK, state.getCell(0, 1)));
        state.setPiece(new Piece(PieceType.KNIGHT, PieceColor.BLACK, state.getCell(0, 6)));
        state.setPiece(new Piece(PieceType.KNIGHT, PieceColor.WHITE, state.getCell(7, 1)));
        state.setPiece(new Piece(PieceType.KNIGHT, PieceColor.WHITE, state.getCell(7, 6)));

        // bishops
        state.setPiece(new Piece(PieceType.BISHOP, PieceColor.BLACK, state.getCell(0, 2)));
        state.setPiece(new Piece(PieceType.BISHOP, PieceColor.BLACK, state.getCell(0, 5)));
        state.setPiece(new Piece(PieceType.BISHOP, PieceColor.WHITE, state.getCell(7, 2)));
        state.setPiece(new Piece(PieceType.BISHOP, PieceColor.WHITE, state.getCell(7, 5)));

        // queens
        state.setPiece(new Piece(PieceType.QUEEN, PieceColor.BLACK, state.getCell(0, 3)));
        state.setPiece(new Piece(PieceType.QUEEN, PieceColor.WHITE, state.getCell(7, 3)));

        // kings
        state.setPiece(new Piece(PieceType.KING, PieceColor.BLACK, state.getCell(0, 4)));
        state.setPiece(new Piece(PieceType.KING, PieceColor.WHITE, state.getCell(7, 4)));
    }
}