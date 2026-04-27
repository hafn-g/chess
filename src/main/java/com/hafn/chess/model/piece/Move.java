package com.hafn.chess.model.piece;

import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.Cell;

import java.util.Set;

public interface Move {
    void execute(BoardState state, Cell clicked);
    void undo(BoardState state);
    Set<Cell> possibleMoves(BoardState state, Cell cell);
}
