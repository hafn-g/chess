package com.hafn.chess.application.port.out;

import com.hafn.chess.domain.model.GameConfig;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.state.BoardState;

import java.util.function.Consumer;

public interface BoardStatePort {
    BoardState getState();

    boolean showPromotionDialog(PieceColor color, Consumer<PieceType> onPromotionSelected);

    void showInfoDialog(String title, String message);

    GameConfig getGameConfig();
}
