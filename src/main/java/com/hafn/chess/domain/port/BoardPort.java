package com.hafn.chess.domain.port;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.HistoryMove;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.Piece;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BoardPort {
    Piece getPiece(Cell cell);

    Map<Cell, Piece> getPieces();

    void addPiece(Piece piece);

    void removePiece(Cell cell);

    int getRows();

    int getCols();

    Cell getCell(int row, int col);

    void addCell(int row, int col, String name);

    void addHistoryMoves(HistoryMove historyMove);

    List<HistoryMove> getHistoryMoves();

    Cell getClickedCell();

    void setClickedCell(Cell clickedCell);

    boolean inBounds(int r, int c);

    boolean isBlackShah();

    boolean isWhiteShah();

    boolean isShah(PieceColor color);

    void setBlackShah(boolean blackShah);

    void setWhiteShah(boolean whiteShah);

    PieceColor getQueue();

    void setQueue(PieceColor queue);

    void nextQueue();

    LocalDateTime getStartGameDateTime();

    int getBlackTime();

    int getWhiteTime();

    void addTime(PieceColor color);

    void stopGame();

    boolean isPauseGame();
}
