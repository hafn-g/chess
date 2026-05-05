package com.hafn.chess.application.usecase;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.piece.Pawn;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.piece.Queen;
import com.hafn.chess.domain.service.CheckRule;
import com.hafn.chess.domain.state.BoardState;
import com.hafn.chess.ui.swing.panel.BoardPanel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class BoardControllerTest {
    private BoardPanel boardPanel;

    @BeforeEach
    void setUp() {
        boardPanel = new BoardPanel(8, 8, 600, PieceColor.WHITE);
    }

    @Test
    void handleClick_whenGamePaused_clearsSelectionAndDoesNothingElse() {
        boardPanel.getState().setPauseGame(true);

        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onEmptyCellWithNoSelection_doesNothing() {
        boardPanel.getBoardController().handleClick(3, 0);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onOwnPieceWithNoSelection_selectsThatPiece() {
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 0),
                        boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 0),
                        boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onAnotherOwnPieceWhileSelected_changesSelectionToNewPiece() {
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 1);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 1),
                        boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 1),
                        boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onEnemyPieceWhileSelected_noMoveAndNoSelectionChange() {
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);
        boardPanel.getBoardController().handleClick(1, 1);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 0),
                        boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 0),
                        boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onEmptyCellNotInPossibleMovesWhileSelected_clearsSelection() {
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);
        boardPanel.getBoardController().handleClick(3, 5);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onEmptyCellInPossibleMovesWhileSelected_executesMove() {
        Cell from = boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 0);
        Cell to = boardPanel.getState().getCell(boardPanel.getState().getRows()-3, 0);

        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-3, 0);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getPiece(from)),
                () -> Assertions.assertNotNull(boardPanel.getState().getPiece(to)),
                () -> Assertions.assertEquals(PieceColor.WHITE, boardPanel.getState().getPiece(to).getColor())
        );
    }

    @Test
    void handleClick_onEnemyPieceInPossibleMovesWhileSelected_executesMoveAndCaptures() {
        boardPanel.getState().addPiece(new Pawn(PieceColor.BLACK, boardPanel.getState().getCell(boardPanel.getState().getRows()-3, 1)));
        CheckRule.detectChecks(boardPanel.getState());

        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-3, 1);

        Cell from = boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 0);
        Cell to = boardPanel.getState().getCell(boardPanel.getState().getRows()-3, 1);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getPiece(from)),
                () -> Assertions.assertNotNull(boardPanel.getState().getPiece(to)),
                () -> Assertions.assertEquals(PieceColor.WHITE, boardPanel.getState().getPiece(to).getColor())
        );
    }

    @Test
    void handleClick_onSelectedPiece_clearsSelection() {
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 0);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_moveThatDoesNotResolveCheck_undoesAndDoesNotSwitchQueue() throws Exception {
        BoardPanel spyPanel = Mockito.spy(boardPanel);
        Mockito.doNothing().when(spyPanel).showInfoDialog(Mockito.anyString(), Mockito.anyString());

        Field controllerField = BoardPanel.class.getDeclaredField("boardController");
        controllerField.setAccessible(true);
        BoardController newController = new BoardController(spyPanel, spyPanel.getRenderer());
        controllerField.set(spyPanel, newController);

        BoardState state = spyPanel.getState();

        state.removePiece(state.getCell(state.getRows() - 2, 4));
        state.addPiece(new Queen(PieceColor.BLACK, state.getCell(state.getRows() - 2, 4)));
        CheckRule.detectChecks(state);

        spyPanel.getBoardController().handleClick(state.getRows() - 2, 0);
        spyPanel.getBoardController().handleClick(state.getRows() - 3, 0);

        Cell attemptedTarget = state.getCell(state.getRows() - 3, 0);

        Assertions.assertAll(
                () -> Assertions.assertNull(spyPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(state.getClickedCell()),
                () -> Assertions.assertNull(state.getPiece(attemptedTarget)),
                () -> Assertions.assertTrue(state.isWhiteShah()),
                () -> Assertions.assertFalse(state.isBlackShah())
        );
    }

    @Test
    void handleClick_moveThatResolvesCheck_executesAndSwitchesQueue() {
        boardPanel.getState().removePiece(boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 4));
        boardPanel.getState().addPiece(new Queen(PieceColor.BLACK, boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 4)));
        CheckRule.detectChecks(boardPanel.getState());

        Assertions.assertAll(
                () -> Assertions.assertTrue(boardPanel.getState().isWhiteShah()),
                () -> Assertions.assertFalse(boardPanel.getState().isBlackShah())
        );

        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-1, 4);
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-2, 4);

        Cell from = boardPanel.getState().getCell(boardPanel.getState().getRows()-1, 4);
        Cell to = boardPanel.getState().getCell(boardPanel.getState().getRows()-2, 4);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell()),
                () -> Assertions.assertEquals(PieceColor.WHITE, boardPanel.getState().getPiece(
                        boardPanel.getState().getCell(boardPanel.getState().getRows() - 2, 4)
                ).getColor()),
                () -> Assertions.assertNull(boardPanel.getState().getPiece(from)),
                () -> Assertions.assertEquals(PieceColor.BLACK, boardPanel.getState().getQueue()),
                () -> Assertions.assertFalse(boardPanel.getState().isWhiteShah()),
                () -> Assertions.assertFalse(boardPanel.getState().isBlackShah())
        );
    }

    @Test
    void handleClick_moveThatGivesCheckToOpponent_setsOpponentShahFlag() {
        boardPanel.getState().removePiece(boardPanel.getState().getCell(1, 4));
        boardPanel.getState().addPiece(new Queen(PieceColor.WHITE, boardPanel.getState().getCell(2, 3)));
        CheckRule.detectChecks(boardPanel.getState());

        boardPanel.getBoardController().handleClick(2, 3);
        boardPanel.getBoardController().handleClick(2, 4);

        Cell from = boardPanel.getState().getCell(2, 3);
        Cell to = boardPanel.getState().getCell(2, 4);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getPiece(from)),
                () -> Assertions.assertSame(PieceColor.WHITE, boardPanel.getState().getPiece(to).getColor()),
                () -> Assertions.assertFalse(boardPanel.getState().isWhiteShah()),
                () -> Assertions.assertTrue(boardPanel.getState().isBlackShah())
        );
    }

    @Test
    void handleClick_moveThatExposesOwnKingToCheck_undoesAndDoesNotSwitchQueue() throws Exception {
        BoardPanel spyPanel = Mockito.spy(boardPanel);
        Mockito.doNothing().when(spyPanel).showInfoDialog(Mockito.anyString(), Mockito.anyString());

        Field controllerField = BoardPanel.class.getDeclaredField("boardController");
        controllerField.setAccessible(true);
        BoardController newController = new BoardController(spyPanel, spyPanel.getRenderer());
        controllerField.set(spyPanel, newController);

        BoardState state = spyPanel.getState();
        state.addPiece(new Queen(PieceColor.BLACK, state.getCell(state.getRows() - 3, 2)));
        CheckRule.detectChecks(state);

        spyPanel.getBoardController().handleClick(state.getRows() - 2, 3);
        spyPanel.getBoardController().handleClick(state.getRows() - 3, 3);

        Cell from = state.getCell(state.getRows() - 2, 3);

        Assertions.assertAll(
                () -> Assertions.assertNull(spyPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(state.getClickedCell()),
                () -> Assertions.assertNotNull(state.getPiece(from)),                // pawn not moved
                () -> Assertions.assertEquals(PieceType.PAWN, state.getPiece(from).getType()),
                () -> Assertions.assertEquals(PieceColor.WHITE, state.getQueue()),  // queue unchanged
                () -> Assertions.assertFalse(state.isWhiteShah()),
                () -> Assertions.assertFalse(state.isBlackShah())
        );
    }

    @Test
    void handleClick_onOwnBlockedPiece_stillSelectsItEvenIfNoMoves() {
        boardPanel.getBoardController().handleClick(boardPanel.getState().getRows()-1, 0);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-1, 0),
                        boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertEquals(
                        boardPanel.getState().getCell(boardPanel.getState().getRows()-1, 0),
                        boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_onEnemyPieceWithNoSelection_doesNothing() {
        boardPanel.getBoardController().handleClick(1, 1);

        Assertions.assertAll(
                () -> Assertions.assertNull(boardPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(boardPanel.getState().getClickedCell())
        );
    }

    @Test
    void handleClick_pawnPromotion_whenReachesLastRank_replacesPawnWithChosenPiece() throws Exception {
        BoardPanel spyPanel = Mockito.spy(boardPanel);

        Mockito.doAnswer(invocation -> {
            Consumer<PieceType> consumer = invocation.getArgument(1);
            consumer.accept(PieceType.QUEEN);
            return true;
        }).when(spyPanel).showPromotionDialog(Mockito.any(PieceColor.class), Mockito.any());

        Field controllerField = BoardPanel.class.getDeclaredField("boardController");
        controllerField.setAccessible(true);
        BoardController newController = new BoardController(spyPanel, spyPanel.getRenderer());
        controllerField.set(spyPanel, newController);

        BoardState state = spyPanel.getState();
        state.removePiece(state.getCell(0, 0));
        state.removePiece(state.getCell(1, 0));
        Pawn pawn = new Pawn(PieceColor.WHITE, state.getCell(1, 0));
        state.addPiece(pawn);
        state.setClickedCell(null);
        CheckRule.detectChecks(state);

        spyPanel.getBoardController().handleClick(1, 0);
        spyPanel.getBoardController().handleClick(0, 0);

        Piece promoted = state.getPiece(state.getCell(0, 0));
        Assertions.assertAll(
                () -> Assertions.assertNotNull(promoted),
                () -> Assertions.assertEquals(PieceType.QUEEN, promoted.getType()),
                () -> Assertions.assertEquals(PieceColor.WHITE, promoted.getColor()),
                () -> Assertions.assertNull(spyPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(state.getClickedCell()),
                () -> Assertions.assertNull(state.getPiece(state.getCell(1, 0)))
        );
    }

    @Test
    void pawnReachesLastRank_withoutPromotion_remainsPawn() throws Exception {
        BoardPanel spyPanel = Mockito.spy(boardPanel);

        Mockito.doAnswer(_ -> false)
                .when(spyPanel).showPromotionDialog(Mockito.any(PieceColor.class), Mockito.any());

        Field controllerField = BoardPanel.class.getDeclaredField("boardController");
        controllerField.setAccessible(true);
        BoardController newController = new BoardController(spyPanel, spyPanel.getRenderer());
        controllerField.set(spyPanel, newController);

        BoardState state = spyPanel.getState();
        state.removePiece(state.getCell(0, 0));
        state.removePiece(state.getCell(1, 0));
        Pawn pawn = new Pawn(PieceColor.WHITE, state.getCell(1, 0));
        state.addPiece(pawn);
        state.setClickedCell(null);
        CheckRule.detectChecks(state);

        spyPanel.getBoardController().handleClick(1, 0);
        spyPanel.getBoardController().handleClick(0, 0);

        Piece promoted = state.getPiece(state.getCell(0, 0));
        Assertions.assertAll(
                () -> Assertions.assertNotNull(promoted),
                () -> Assertions.assertEquals(PieceType.PAWN, promoted.getType()),
                () -> Assertions.assertEquals(PieceColor.WHITE, promoted.getColor()),
                () -> Assertions.assertNull(spyPanel.getRenderer().getSelectedCell()),
                () -> Assertions.assertNull(state.getClickedCell()),
                () -> Assertions.assertNull(state.getPiece(state.getCell(1, 0)))
        );
    }
}
