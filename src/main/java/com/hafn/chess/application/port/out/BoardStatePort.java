package com.hafn.chess.application.port.out;

import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.state.BoardState;

import java.util.function.Consumer;

public interface BoardStatePort {
    BoardState getState();

    void showPromotionDialog(PieceColor color, Consumer<PieceType> onPromotionSelected);

    void showInfoDialog(String title, String message);
}
