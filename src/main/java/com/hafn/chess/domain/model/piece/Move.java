package com.hafn.chess.domain.model.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.port.BoardPort;

import java.util.Set;

public interface Move {
    void execute(BoardPort state, Cell clicked);
    void undo(BoardPort state);
    Set<Cell> possibleMoves(BoardPort state, Cell cell);
}
