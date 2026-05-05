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

public class BishopTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void bishopFromCenter_shouldReturnAllDiagonalCells() {
        Piece bishop = new Bishop(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(bishop);
        bishop.getNewPossibleMoves(boardPort);

        Set<Cell> moves = bishop.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,0))), // up-left to A8
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,7))), // up-right to B1
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,1))), // down-left to H2
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,7))), // down-right to H1
                () -> Assertions.assertEquals(13, moves.size()) // total diagonal moves from center
        );
    }

    @Test
    void bishopBlockedByOwnPiece_shouldNotIncludeBlockedCell() {
        Piece bishop = new Bishop(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece own = new Bishop(PieceColor.WHITE, boardPort.getCell(2,6)); // own piece on up-right diagonal
        boardPort.addPiece(bishop);
        boardPort.addPiece(own);
        bishop.getNewPossibleMoves(boardPort);

        Set<Cell> moves = bishop.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,5))),
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(2,6)))
        );
    }

    @Test
    void bishopBlockedByEnemy_shouldIncludeEnemyCellButNotBeyond() {
        Piece bishop = new Bishop(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece enemy = new Bishop(PieceColor.BLACK, boardPort.getCell(2,6));
        boardPort.addPiece(bishop);
        boardPort.addPiece(enemy);
        bishop.getNewPossibleMoves(boardPort);

        Set<Cell> moves = bishop.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,5))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,6))), // capture enemy
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(1,7)))
        );
    }

    @Test
    void bishopInCorner_shouldMoveOnlyAlongOneDiagonal() {
        Piece bishop = new Bishop(PieceColor.WHITE, boardPort.getCell(0,0)); // A8
        boardPort.addPiece(bishop);
        bishop.getNewPossibleMoves(boardPort);

        Set<Cell> moves = bishop.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,1))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,7))), // H1
                () -> Assertions.assertEquals(7, moves.size()) // one diagonal length 7
        );
    }
}