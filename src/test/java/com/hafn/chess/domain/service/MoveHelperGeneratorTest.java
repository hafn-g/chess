package com.hafn.chess.domain.service;

import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.Bishop;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.piece.Rook;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.state.BoardState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class MoveHelperGeneratorTest {

    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void slidingMoves_emptyBoard_rookFromCenter_reachesAllEdges() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4, 4));
        boardPort.addPiece(rook);

        int[][] dirs = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, rook, dirs);

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0, 4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7, 4))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4, 0))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4, 7))),
                () -> Assertions.assertEquals(14, moves.size())
        );
    }

    @Test
    void slidingMoves_rookBlockedByOwnPiece_stopsBeforeIt() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4, 4));
        Piece ownPiece = new Rook(PieceColor.WHITE, boardPort.getCell(4, 6));
        boardPort.addPiece(rook);
        boardPort.addPiece(ownPiece);

        int[][] dirs = {{0, 1}};

        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, rook, dirs);

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4, 5))),
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4, 6))),
                () -> Assertions.assertEquals(1, moves.size())
        );
    }

    @Test
    void slidingMoves_rookBlockedByEnemy_includesEnemyAndStops() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4, 4));
        Piece enemy = new Rook(PieceColor.BLACK, boardPort.getCell(4, 6));
        boardPort.addPiece(rook);
        boardPort.addPiece(enemy);

        int[][] dirs = {{0, 1}};

        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, rook, dirs);

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4, 5))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(4, 6))),
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(4, 7))),
                () -> Assertions.assertEquals(2, moves.size())
        );
    }

    @Test
    void slidingMoves_noDirections_returnsEmptySet() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(4, 4));
        boardPort.addPiece(rook);

        int[][] emptyDirs = {};

        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, rook, emptyDirs);

        Assertions.assertTrue(moves.isEmpty());
    }

    @Test
    void slidingMoves_pieceInCorner_movesOnlyInsideBoard() {
        Piece rook = new Rook(PieceColor.WHITE, boardPort.getCell(0, 0));
        boardPort.addPiece(rook);

        int[][] dirs = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, rook, dirs);

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(1, 0))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(7, 0))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0, 1))),
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(0, 7))),
                () -> Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> moves.contains(boardPort.getCell(-1, 0))),
                () -> Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> moves.contains(boardPort.getCell(0, -1))),
                () -> Assertions.assertEquals(14, moves.size())
        );
    }

    @Test
    void slidingMoves_surroundedByOwnPieces_returnsEmptySet() {
        Piece king = new Rook(PieceColor.WHITE, boardPort.getCell(4, 4));
        boardPort.addPiece(king);

        boardPort.addPiece(new Rook(PieceColor.WHITE, boardPort.getCell(3, 4)));
        boardPort.addPiece(new Rook(PieceColor.WHITE, boardPort.getCell(5, 4)));
        boardPort.addPiece(new Rook(PieceColor.WHITE, boardPort.getCell(4, 3)));
        boardPort.addPiece(new Rook(PieceColor.WHITE, boardPort.getCell(4, 5)));

        int[][] dirs = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, king, dirs);

        Assertions.assertTrue(moves.isEmpty());
    }

    @Test
    void slidingMoves_twoEnemiesOnSameLine_onlyFirstCapturable() {
        Piece bishop = new Bishop(PieceColor.WHITE, boardPort.getCell(4,4));
        Piece enemy1 = new Bishop(PieceColor.BLACK, boardPort.getCell(3,3));
        Piece enemy2 = new Bishop(PieceColor.BLACK, boardPort.getCell(2,2));
        boardPort.addPiece(bishop);
        boardPort.addPiece(enemy1);
        boardPort.addPiece(enemy2);

        int[][] dirs = {{-1,-1}}; // only up-left direction
        Set<Cell> moves = MoveHelperGenerator.slidingMoves(boardPort, bishop, dirs);

        Assertions.assertAll(
                () -> Assertions.assertTrue(moves.contains(boardPort.getCell(3,3))),
                () -> Assertions.assertFalse(moves.contains(boardPort.getCell(2,2)))
        );
    }
}