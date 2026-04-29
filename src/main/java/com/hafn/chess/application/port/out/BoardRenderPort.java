package com.hafn.chess.application.port.out;

import com.hafn.chess.domain.state.BoardState;
import com.hafn.chess.ui.swing.model.BoardMetrics;

public interface BoardRenderPort {
    BoardMetrics getMetrics();

    BoardState getState();

    void repaintBoard();
}

