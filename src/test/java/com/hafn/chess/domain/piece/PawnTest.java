package com.hafn.chess.domain.piece;

import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.state.BoardState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

public class PawnTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void whitePawnFromInitialRow_canMoveOneOrTwoStepsForward() {
        Pawn pawn = new Pawn(PieceColor.WHITE, boardPort.getCell(6,4));
        boardPort.addPiece(pawn);
        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,4))),
                () -> Assertions.assertEquals(2, moves.size())
        );
    }

    @Test
    void whitePawnAfterFirstMove_cannotDoubleMove() throws Exception {
        Pawn pawn = new Pawn(PieceColor.WHITE, boardPort.getCell(5,4));
        boardPort.addPiece(pawn);
        Field field = Pawn.class.getDeclaredField("isMoved");
        field.setAccessible(true);
        field.setBoolean(pawn, true);

        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,4))), // one step
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(3,4))), // no double
                () -> Assertions.assertEquals(1, moves.size())
        );
    }

    @Test
    void whitePawnBlockedByOwnPieceForward_noForwardMoves() {
        Pawn pawn = new Pawn(PieceColor.WHITE, boardPort.getCell(6,4));
        Piece own = new Pawn(PieceColor.WHITE, boardPort.getCell(5,4));
        boardPort.addPiece(pawn);
        boardPort.addPiece(own);
        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(5,4))),
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4,4))),
                () -> Assertions.assertEquals(0, moves.size())
        );
    }

    @Test
    void whitePawnCanCaptureEnemyDiagonally() {
        Pawn pawn = new Pawn(PieceColor.WHITE, boardPort.getCell(6,4));
        Piece enemy1 = new Pawn(PieceColor.BLACK, boardPort.getCell(5,3));
        Piece enemy2 = new Pawn(PieceColor.BLACK, boardPort.getCell(5,5));
        boardPort.addPiece(pawn);
        boardPort.addPiece(enemy1);
        boardPort.addPiece(enemy2);
        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,3))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,5))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,4))),
                () -> Assertions.assertEquals(4, moves.size())
        );
    }

    @Test
    void whitePawnCannotCaptureOwnPieceDiagonally() {
        Pawn pawn = new Pawn(PieceColor.WHITE, boardPort.getCell(6,4));
        Piece own = new Pawn(PieceColor.WHITE, boardPort.getCell(5,5));
        boardPort.addPiece(pawn);
        boardPort.addPiece(own);
        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(5,5))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,4)))
        );
    }

    @Test
    void whitePawnAtLeftEdge_cannotCaptureOutside() {
        Pawn pawn = new Pawn(PieceColor.WHITE, boardPort.getCell(6,0));
        boardPort.addPiece(pawn);
        Piece enemy = new Pawn(PieceColor.BLACK, boardPort.getCell(5,1));
        boardPort.addPiece(enemy);
        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,1))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(5,0))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,0))),
                () -> Assertions.assertEquals(3, moves.size())
        );
    }

    @Test
    void blackPawnFromInitialRow_canMoveOneOrTwoStepsDown() {
        Pawn pawn = new Pawn(PieceColor.BLACK, boardPort.getCell(1,4));
        boardPort.addPiece(pawn);
        pawn.getNewPossibleMoves(boardPort);
        Set<Cell> moves = pawn.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(2,4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,4))),
                () -> Assertions.assertEquals(2, moves.size())
        );
    }
}