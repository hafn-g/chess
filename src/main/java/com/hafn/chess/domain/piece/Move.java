package com.hafn.chess.domain.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.port.BoardPort;

public interface Move {
    void execute(BoardPort state, Cell clicked);
    void undo(BoardPort state);
    void getNewPossibleMoves(BoardPort state);
}
