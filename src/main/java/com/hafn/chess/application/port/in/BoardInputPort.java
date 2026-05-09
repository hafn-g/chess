package com.hafn.chess.application.port.in;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;

public interface BoardInputPort {
    void handleClick(int row, int col);

    void move(Cell from, Cell to);

    PieceColor getMyMultiplayerColor();

    void clearSelection();
}
