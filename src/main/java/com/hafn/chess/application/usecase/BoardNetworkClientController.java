package com.hafn.chess.application.usecase;

import com.hafn.chess.application.port.in.BoardInputPort;
import com.hafn.chess.application.port.out.BoardStatePort;
import com.hafn.chess.application.port.out.SelectionPort;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.GameType;
import com.hafn.chess.domain.model.HistoryMove;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.Checker;
import com.hafn.chess.domain.piece.Pawn;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.piece.Queen;
import com.hafn.chess.domain.service.CheckRule;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@EqualsAndHashCode
@ToString
public class BoardNetworkClientController implements BoardInputPort {
    private final BoardStatePort boardStatePort;
    private final SelectionPort selectionPort;

    @Getter
    private final PieceColor myMultiplayerColor;

    public BoardNetworkClientController(BoardStatePort boardStatePort, SelectionPort selectionPort, PieceColor myMultiplayerColor) {
        this.boardStatePort = boardStatePort;
        this.selectionPort = selectionPort;
        this.myMultiplayerColor = myMultiplayerColor;
    }

    @Override
    public void move(Cell from, Cell to) {
        boardStatePort.getState().setClickedCell(from);
        Piece piece = boardStatePort.getState().getPiece(from);
        piece.execute(boardStatePort.getState(), to);
        handlePromotionIfNeeded(piece, to);
        CheckRule.detectChecks(boardStatePort.getState());
    }

    /**
     *  Works only with cells
     */
    @Override
    public void handleClick(int row, int col) {
        if (boardStatePort.getState().isPauseGame()) {
            clearSelection();
            return;
        }
        if (!boardStatePort.getState().getQueue().equals(myMultiplayerColor)) {
            clearSelection();
            return;
        }

        Cell clicked = boardStatePort.getState().getCell(row, col);

        log.trace("Board cell clicked: {}", clicked);

        Piece piece = boardStatePort.getState().getPiece(boardStatePort.getState().getClickedCell());
        boolean isMove = false;
        if (piece != null) {
            isMove = piece.getPossibleMoves().contains(clicked);
        }
        if (isMove) {
            if (boardStatePort.getGameConfig().getGameType().equals(GameType.CHESS)) {
                toMoveChess(piece, clicked);
                clearSelection();
            } else {
                toMoveCheckers((Checker) piece, clicked);
            }
        } else {
            toSelected(clicked);
        }
    }

    @Override
    public void clearSelection() {
        selectionPort.clearSelectedCell();
        boardStatePort.getState().setClickedCell(null);
    }

    private void toMoveCheckers(Checker piece, Cell clicked) {
        Cell fromCell = piece.getCell();
        log.trace("Attempt to move checker {} to {}", piece, clicked);

        if (boardStatePort.getState().getPiece(clicked) != null) return;

        piece.execute(boardStatePort.getState(), clicked);
        HistoryMove lastHistoryMove = boardStatePort.getState().getHistoryMoves().getLast();

        if (lastHistoryMove.getPieceDestroyed() != null) {
            if (piece.isMoveIsAvailableAfterWalking(boardStatePort.getState())) {
                selectionPort.setSelectedCell(piece.getCell());
                log.info("Move successful from the queue: {} -> {}", fromCell.getName(), clicked.getName());

                boardStatePort.getState().getPieces().forEach((_, statePiece) -> {
                    if (!statePiece.equals(piece)) {
                        statePiece.setPossibleMoves(Set.of());
                    }
                });

                return;
            }
        }

        boardStatePort.getState().nextQueue();
        boardStatePort.getState().addTime(piece.getColor());
        CheckRule.generateAllPossibleMoves(boardStatePort.getState());
        clearSelection();

        log.info("Last move successful: {} -> {}", fromCell.getName(), clicked.getName());
    }

    private void toMoveChess(Piece piece, Cell clicked) {
        Cell fromCell = piece.getCell();
        Piece clickedPiece = boardStatePort.getState().getPiece(clicked);
        log.trace("Attempt to move piece {} to {}", piece, clicked);
        // Check if the current player's king is in check
        if (boardStatePort.getState().isWhiteShah() || boardStatePort.getState().isBlackShah()) {
            // King is in check – try to move out of check
            piece.execute(boardStatePort.getState(), clicked);
            CheckRule.detectChecks(boardStatePort.getState());

            if (boardStatePort.getState().isWhiteShah() || boardStatePort.getState().isBlackShah()) {
                // After the move, the king is still in check – illegal move
                piece.undo(boardStatePort.getState());
                CheckRule.detectChecks(boardStatePort.getState());
                log.warn("Move failed: King would still be in check.");
                boardStatePort.showInfoDialog("Move failed", "King would still be in check");
            } else {
                // Move successfully resolved the check
                handlePromotionIfNeeded(piece, clicked);
                boardStatePort.getState().nextQueue();
                if (clickedPiece != null) {
                    log.info("Move successful (King is no longer in check): {} -> {} ({} was captured by {})", fromCell.getName(), clicked.getName(), clickedPiece, piece);
                } else {
                    log.info("Move successful (King is no longer in check): {} -> {}", fromCell.getName(), clicked.getName());
                }
                boardStatePort.getState().addTime(piece.getColor());
            }
        } else {
            // No check – normal move
            piece.execute(boardStatePort.getState(), clicked);
            CheckRule.detectChecks(boardStatePort.getState());

            if (boardStatePort.getState().isShah(piece.getColor())) {
                // The move puts the moving player's own king in check – illegal
                piece.undo(boardStatePort.getState());
                CheckRule.detectChecks(boardStatePort.getState());
                log.warn("Move failed: Would put own king in check.");
                boardStatePort.showInfoDialog("Move failed", "Would put own king in check");
            } else {
                // Legal move
                handlePromotionIfNeeded(piece, clicked);
                boardStatePort.getState().nextQueue();
                if (clickedPiece != null) {
                    log.info("Move successful: {} -> {} ({} was captured by {})", fromCell.getName(), clicked.getName(), clickedPiece, piece);
                } else {
                    log.info("Move successful: {} -> {}", fromCell.getName(), clicked.getName());
                }
                boardStatePort.getState().addTime(piece.getColor());
            }
        }
    }

    // Checking and handling pawn promotions
    private void handlePromotionIfNeeded(Piece piece, Cell cell) {
        if (piece instanceof Pawn) {
            int lastRow = (piece.getColor() == PieceColor.WHITE) ? 0 : boardStatePort.getState().getRows() - 1;
            if (cell.getRow() == lastRow && boardStatePort.getState() != null) {
                PieceColor color = piece.getColor();
                Piece promoted = new Queen(color, cell);

                boardStatePort.getState().removePiece(cell);
                boardStatePort.getState().addPiece(promoted);
                CheckRule.detectChecks(boardStatePort.getState());
                log.info("Pawn of color {} ({}) promoted to piece {}", color, cell, promoted.getType());
            }
        }
    }

    private void toSelected(Cell clicked) {
        Piece clickedCellPiece = boardStatePort.getState().getPiece(clicked);
        if (clickedCellPiece != null) {
            if (!clickedCellPiece.getColor().equals(boardStatePort.getState().getQueue())) {
                return;
            }
        }

        // nothing selected yet
        if (selectionPort.getSelectedCell() == null) {
            if (boardStatePort.getState().getPiece(clicked) != null) {
                selectionPort.setSelectedCell(clicked);
                boardStatePort.getState().setClickedCell(clicked);
                log.debug("Cell {} was clicked", clicked);
            }
        } else { // already selected
            // same cell -> unselect
            if (selectionPort.getSelectedCell() == clicked) {
                clearSelection();
                log.debug("Cell {} was re-clicked", clicked);
            } else if (boardStatePort.getState().getPiece(clicked) != null) {
                selectionPort.setSelectedCell(clicked);
                boardStatePort.getState().setClickedCell(clicked);
                log.debug("Different cell clicked: previous={}, new={}", selectionPort.getSelectedCell(), clicked);
            } else {
                clearSelection();
                log.debug("Selection cleared because clicked on empty cell: {}", clicked);
            }
        }
    }
}
