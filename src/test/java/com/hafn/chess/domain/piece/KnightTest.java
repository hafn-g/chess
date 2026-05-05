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

public class KnightTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void knightFromCenter_shouldHaveEightMoves() {
        Knight knight = new Knight(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(knight);
        knight.getNewPossibleMoves(boardPort);

        Set<Cell> moves = knight.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,3))), // up-left
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,5))), // up-right
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,2))), // left-up
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,6))), // right-up
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,2))), // left-down
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,6))), // right-down
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(6,3))), // down-left
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(6,5))), // down-right
                () -> Assertions.assertEquals(8, moves.size())
        );
    }

    @Test
    void knight_jumpsOverPieces_shouldStillMove() {
        Knight knight = new Knight(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece blocker1 = new Pawn(PieceColor.WHITE, boardPort.getCell(3,3));
        Piece blocker2 = new Pawn(PieceColor.BLACK, boardPort.getCell(2,4));
        boardPort.addPiece(knight);
        boardPort.addPiece(blocker1);
        boardPort.addPiece(blocker2);
        knight.getNewPossibleMoves(boardPort);

        Set<Cell> moves = knight.getPossibleMoves();

        Assertions.assertEquals(8, moves.size()); // knights ignore obstacles
    }

    @Test
    void knight_canCaptureEnemyEvenIfBlockedByOthers() {
        Knight knight = new Knight(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece enemy = new Knight(PieceColor.BLACK, boardPort.getCell(2,3));
        boardPort.addPiece(knight);
        boardPort.addPiece(enemy);
        knight.getNewPossibleMoves(boardPort);

        Set<Cell> moves = knight.getPossibleMoves();

        Assertions.assertTrue(moves.contains(boardPort.getCell(2,3))); // capture enemy
    }

    @Test
    void knight_nearCorner_hasFewerMoves() {
        Knight knight = new Knight(PieceColor.WHITE, boardPort.getCell(0,0)); // A8
        boardPort.addPiece(knight);
        knight.getNewPossibleMoves(boardPort);

        Set<Cell> moves = knight.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,2))), // only one possible
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,1))), // second possible
                () -> Assertions.assertEquals(2, moves.size())
        );
    }

    @Test
    void knight_onEdge_hasLimitedMoves() {
        Knight knight = new Knight(PieceColor.WHITE, boardPort.getCell(0,4)); // E8
        boardPort.addPiece(knight);
        knight.getNewPossibleMoves(boardPort);

        Set<Cell> moves = knight.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,2))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,6))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,3))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,5))),
                () -> Assertions.assertEquals(4, moves.size())
        );
    }
}