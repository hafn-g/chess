package com.hafn.chess.domain.piece;

import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.state.BoardState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PieceTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);
    }

    @Test
    void execute_movesRookToEmptyCell_updatesBoardAndHistory() {
        Rook rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(rook);
        Cell target = boardPort.getCell(4,5);
        boardPort.setClickedCell(rook.getCell());

        rook.execute(boardPort, target);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPort.getPiece(boardPort.getCell(4,4))), // old cell empty
                () -> Assertions.assertSame(rook, boardPort.getPiece(target)),           // piece moved to target
                () -> Assertions.assertEquals(1, boardPort.getHistoryMoves().size())     // history recorded
        );
    }

    @Test
    void execute_capturesEnemyRook_removesEnemyAndRecordsCapture() {
        Rook whiteRook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        Rook blackRook = new Rook(PieceColor.BLACK, boardPort.getCell(4,5));
        boardPort.addPiece(whiteRook);
        boardPort.addPiece(blackRook);
        Cell target = boardPort.getCell(4,5);
        boardPort.setClickedCell(whiteRook.getCell());

        whiteRook.execute(boardPort, target);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPort.getPiece(boardPort.getCell(4,4))),  // old cell empty
                () -> Assertions.assertSame(whiteRook, boardPort.getPiece(target)),      // white rook captures
                () -> Assertions.assertEquals(1, boardPort.getHistoryMoves().size()),    // history added
                () -> Assertions.assertNotNull(boardPort.getHistoryMoves().getLast().getPieceDestroyed()), // captured piece stored
                () -> Assertions.assertSame(blackRook, boardPort.getHistoryMoves().getLast().getPieceDestroyed()) // correct enemy
        );
    }

    @Test
    void execute_attemptsToCaptureKing_doesNotMove() {
        Rook rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        King blackKing = new King(PieceColor.BLACK, boardPort.getCell(4,5));
        boardPort.addPiece(rook);
        boardPort.addPiece(blackKing);
        Cell target = boardPort.getCell(4,5);
        boardPort.setClickedCell(rook.getCell());

        rook.execute(boardPort, target);

        Assertions.assertAll(
                () -> Assertions.assertSame(rook, boardPort.getPiece(boardPort.getCell(4,4))), // rook unchanged
                () -> Assertions.assertSame(blackKing, boardPort.getPiece(target)),           // king unchanged
                () -> Assertions.assertTrue(boardPort.getHistoryMoves().isEmpty())            // no history added
        );
    }

    @Test
    void undo_afterMove_restoresRookToOriginalPosition() {
        Rook rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(rook);
        Cell target = boardPort.getCell(4,5);
        boardPort.setClickedCell(rook.getCell());
        rook.execute(boardPort, target);

        rook.undo(boardPort);

        Assertions.assertAll(
                () -> Assertions.assertSame(rook, boardPort.getPiece(boardPort.getCell(4,4))), // back to original
                () -> Assertions.assertNull(boardPort.getPiece(target))                       // target empty
        );
    }

    @Test
    void undo_afterCapture_restoresBothRookAndEnemy() {
        Rook whiteRook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        Rook blackRook = new Rook(PieceColor.BLACK, boardPort.getCell(4,5));
        boardPort.addPiece(whiteRook);
        boardPort.addPiece(blackRook);
        Cell target = boardPort.getCell(4,5);
        boardPort.setClickedCell(whiteRook.getCell());
        whiteRook.execute(boardPort, target);

        whiteRook.undo(boardPort);

        Assertions.assertAll(
                () -> Assertions.assertSame(whiteRook, boardPort.getPiece(boardPort.getCell(4,4))), // white rook restored
                () -> Assertions.assertSame(blackRook, boardPort.getPiece(boardPort.getCell(4,5))), // enemy restored
                () -> Assertions.assertTrue(boardPort.getHistoryMoves().isEmpty())                 // history cleared
        );
    }

    @Test
    void undo_withEmptyHistory_doesNothing() {
        Rook rook = new Rook(PieceColor.WHITE, boardPort.getCell(4,4));
        boardPort.addPiece(rook);
        Cell original = rook.getCell();

        rook.undo(boardPort); // no history, should not throw

        Assertions.assertSame(rook, boardPort.getPiece(original)); // piece unchanged
    }
}