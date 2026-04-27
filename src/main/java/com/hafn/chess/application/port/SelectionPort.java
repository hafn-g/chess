package com.hafn.chess.application.port;

import com.hafn.chess.domain.model.Cell;

public interface SelectionPort {
    Cell getSelectedCell();

    void setSelectedCell(Cell cell);

    void clearSelectedCell();
}

