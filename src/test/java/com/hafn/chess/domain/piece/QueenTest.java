package com.hafn.chess.domain.piece;

import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.state.BoardState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class QueenTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void queenFromCenter_shouldReturnAllHorizontalVerticalAndDiagonalCells() {
        Piece queen = new Queen(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(queen);
        queen.getNewPossibleMoves(boardPort);

        Set<Cell> moves = queen.getPossibleMoves();

        Assertions.assertAll(
                // horizontal and vertical
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,4))), // up
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,4))), // down
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,0))), // left
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,7))), // right
                // diagonals
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,0))), // up-left
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,7))), // up-right (B1)
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,1))), // down-left (H2)
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,7))), // down-right (H1)
                () -> Assertions.assertEquals(27, moves.size()) // 14 (orthogonal) + 13 (diagonal)
        );
    }

    @Test
    void queenBlockedByOwnPiece_shouldNotIncludeBlockedCell() {
        Piece queen = new Queen(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece ownRight = new Queen(PieceColor.WHITE, boardPort.getCell(4,6)); // right
        Piece ownUpRight = new Queen(PieceColor.WHITE, boardPort.getCell(3,5)); // up-right
        boardPort.addPiece(queen);
        boardPort.addPiece(ownRight);
        boardPort.addPiece(ownUpRight);
        queen.getNewPossibleMoves(boardPort);

        Set<Cell> moves = queen.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))), // free right
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4,6))), // blocked by own right
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,3))), // free up-left
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(3,5))) // blocked by own up-right
        );
    }

    @Test
    void queenBlockedByEnemy_shouldIncludeEnemyCellsButNotBeyond() {
        Piece queen = new Queen(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece enemyRight = new Queen(PieceColor.BLACK, boardPort.getCell(4,6));
        Piece enemyUpRight = new Queen(PieceColor.BLACK, boardPort.getCell(2,6));
        boardPort.addPiece(queen);
        boardPort.addPiece(enemyRight);
        boardPort.addPiece(enemyUpRight);
        queen.getNewPossibleMoves(boardPort);

        Set<Cell> moves = queen.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))), // free
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,6))), // capture enemy right
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4,7))), // beyond captured enemy
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,5))), // free on up-right diag
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,6))), // capture enemy up-right
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(1,7))) // beyond captured enemy
        );
    }

    @Test
    void queenInCorner_shouldMoveAlongTwoDirections() {
        Piece queen = new Queen(PieceColor.WHITE, boardPort.getCell(0,0)); // A8
        boardPort.addPiece(queen);
        queen.getNewPossibleMoves(boardPort);

        Set<Cell> moves = queen.getPossibleMoves();

        Assertions.assertAll(
                // moves like rook from corner: down and right
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,0))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,0))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,1))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,7))),
                // moves like bishop from corner: only down-right diagonal
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,1))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,7))),
                () -> Assertions.assertEquals(21, moves.size()) // 7 down + 7 right + 7 diagonal = 21
        );
    }
}