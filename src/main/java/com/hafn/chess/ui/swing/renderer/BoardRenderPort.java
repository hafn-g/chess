package com.hafn.chess.ui.swing.renderer;

import com.hafn.chess.application.BoardState;
import com.hafn.chess.ui.swing.model.BoardMetrics;

public interface BoardRenderPort {
    BoardMetrics getMetrics();

    BoardState getState();

    void repaintBoard();
}

