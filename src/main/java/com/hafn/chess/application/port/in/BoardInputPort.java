package com.hafn.chess.application.port.in;

public interface BoardInputPort {
    void handleClick(int row, int col);

    void clearSelection();
}
