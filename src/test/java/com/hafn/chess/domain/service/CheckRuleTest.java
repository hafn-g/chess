package com.hafn.chess.domain.service;

import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.King;
import com.hafn.chess.domain.piece.Rook;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.state.BoardState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CheckRuleTest {
    private BoardPort boardPort;

    @BeforeEach
    void setUp() {
        boardPort = new BoardState(8, 8, 600, PieceColor.WHITE);
        BoardInitializer.initCells(boardPort);

        King whiteKing = new King(PieceColor.WHITE, boardPort.getCell(boardPort.getRows()-1, 4));
        King blackKing = new King(PieceColor.BLACK, boardPort.getCell(0, 4));

        boardPort.addPiece(whiteKing);
        boardPort.addPiece(blackKing);
    }

    @Test
    void detectChecks_noChecks_shouldSetBothFlagsFalse() {
        CheckRule.detectChecks(boardPort);

        Assertions.assertAll(
                () -> Assertions.assertFalse(boardPort.isWhiteShah()),
                () -> Assertions.assertFalse(boardPort.isBlackShah())
        );
    }

    @Test
    void detectChecks_whiteKingInCheck_shouldSetWhiteShahTrue() {
        Rook rook = new Rook(PieceColor.BLACK, boardPort.getCell(boardPort.getRows()-3, 4));
        boardPort.addPiece(rook);

        CheckRule.detectChecks(boardPort);

        Assertions.assertAll(
                () -> Assertions.assertTrue(boardPort.isWhiteShah()),
                () -> Assertions.assertFalse(boardPort.isBlackShah())
        );
    }

    @Test
    void detectChecks_blackKingInCheck_shouldSetBlackShahTrue() {
        Rook rook = new Rook(PieceColor.WHITE, boardPort.getCell(2, 4));
        boardPort.addPiece(rook);

        CheckRule.detectChecks(boardPort);

        Assertions.assertAll(
                () -> Assertions.assertFalse(boardPort.isWhiteShah()),
                () -> Assertions.assertTrue(boardPort.isBlackShah())
        );
    }
}
