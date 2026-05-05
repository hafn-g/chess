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

public class KingTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void kingFromCenter_shouldMoveToAllEightAdjacentCells() {
        Piece king = new King(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(king);
        king.getNewPossibleMoves(boardPort);

        Set<Cell> moves = king.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,3))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,5))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,3))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,3))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,5))),
                () -> Assertions.assertEquals(8, moves.size())
        );
    }

    @Test
    void kingBlockedByOwnPiece_shouldNotIncludeThatCell() {
        Piece king = new King(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece own = new King(PieceColor.WHITE, boardPort.getCell(3,4));
        boardPort.addPiece(king);
        boardPort.addPiece(own);
        king.getNewPossibleMoves(boardPort);

        Set<Cell> moves = king.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(3,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,3))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))),
                () -> Assertions.assertEquals(7, moves.size())
        );
    }

    @Test
    void kingBlockedByEnemy_shouldIncludeEnemyCell() {
        Piece king = new King(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece enemy = new King(PieceColor.BLACK, boardPort.getCell(3,4));
        boardPort.addPiece(king);
        boardPort.addPiece(enemy);
        king.getNewPossibleMoves(boardPort);

        Set<Cell> moves = king.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,4))), // capture
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))),
                () -> Assertions.assertEquals(8, moves.size())
        );
    }

    @Test
    void kingOnEdge_shouldNotMoveOutsideBoard() {
        Piece king = new King(PieceColor.WHITE, boardPort.getCell(0,4)); // E8
        boardPort.addPiece(king);
        king.getNewPossibleMoves(boardPort);

        Set<Cell> moves = king.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,3))), // left
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,5))), // right
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,3))), // down-left
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,4))), // down
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,5))), // down-right
                () -> Assertions.assertEquals(5, moves.size())
        );
    }

    @Test
    void kingInCorner_shouldHaveOnlyThreeMoves() {
        Piece king = new King(PieceColor.WHITE, boardPort.getCell(0,0)); // A8
        boardPort.addPiece(king);
        king.getNewPossibleMoves(boardPort);

        Set<Cell> moves = king.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,1))), // right
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,0))), // down
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,1))), // down-right
                () -> Assertions.assertEquals(3, moves.size())
        );
    }
}