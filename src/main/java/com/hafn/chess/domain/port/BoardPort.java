package com.hafn.chess.domain.port;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.HistoryMove;
import com.hafn.chess.domain.model.piece.Piece;

public interface BoardPort {
    Piece getPiece(Cell cell);

    void addPiece(Piece piece);

    void removePiece(Cell cell);

    Cell getCell(int row, int col);

    void addHistoryMoves(HistoryMove historyMove);

    Cell getClickedCell();

    void setClickedCell(Cell clickedCell);

    boolean inBounds(int r, int c);
}

