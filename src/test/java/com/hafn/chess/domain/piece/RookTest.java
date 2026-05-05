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

public class RookTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void rookFromCenter_shouldReturnAllHorizontalAndVerticalCells() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(rook);
        rook.getNewPossibleMoves(boardPort);

        Set<Cell> moves = rook.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,4))), // up to A8
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,4))), // down to E1
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,0))), // left to A4
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,7))), // right to H4
                () -> Assertions.assertEquals(14, moves.size())
        );
    }

    @Test
    void rookBlockedByOwnPiece_shouldNotIncludeBlockedCell() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece own = new Rook(PieceColor.WHITE, boardPort.getCell(4,6));
        boardPort.addPiece(rook);
        boardPort.addPiece(own);
        rook.getNewPossibleMoves(boardPort);

        Set<Cell> moves = rook.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))), // F4
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4,6))) // G4
        );
    }

    @Test
    void rookBlockedByEnemy_shouldIncludeEnemyCellButNotBeyond() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece enemy = new Rook(PieceColor.BLACK, boardPort.getCell(4,6));
        boardPort.addPiece(rook);
        boardPort.addPiece(enemy);
        rook.getNewPossibleMoves(boardPort);

        Set<Cell> moves = rook.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,5))), // F4
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4,6))), // capture G4
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4,7))) // H4
        );
    }

    @Test
    void rookInCorner_shouldMoveOnlyAlongTwoDirections() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(0,0)); // A8
        boardPort.addPiece(rook);
        rook.getNewPossibleMoves(boardPort);

        Set<Cell> moves = rook.getPossibleMoves();

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1,0))), // down one
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7,0))), // down to H8
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,1))), // right one
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0,7))), // right to A1
                () -> Assertions.assertEquals(14, moves.size())
        );
    }
}