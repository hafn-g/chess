package com.hafn.chess.application.usecase;

import com.hafn.chess.application.port.in.BoardInputPort;
import com.hafn.chess.application.port.out.BoardStatePort;
import com.hafn.chess.application.port.out.SelectionPort;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.hafn.chess.domain.service.CheckRule.checkAllPiece;

@Slf4j
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class BoardController implements BoardInputPort {
    private final BoardStatePort boardStatePort;
    private final SelectionPort selectionPort;

    /**
     *  Works only with cells
     */
    @Override
    public void handleClick(int row, int col) {
        if (boardStatePort.getState().isPauseGame()) {
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
            toMove(piece, clicked);
            clearSelection();
        } else {
            toSelected(clicked);
        }
    }

    @Override
    public void clearSelection() {
        selectionPort.clearSelectedCell();
        boardStatePort.getState().setClickedCell(null);
    }

    private void toMove(Piece piece, Cell clicked) {
        Cell fromCell = piece.getCell();
        Piece clickedPiece = boardStatePort.getState().getPiece(clicked);
        log.trace("Attempt to move piece {} to {}", piece, clicked);
        // Check if the current player's king is in check
        if (boardStatePort.getState().isWhiteShah() || boardStatePort.getState().isBlackShah()) {
            // King is in check – try to move out of check
            piece.execute(boardStatePort.getState(), clicked);
            checkAllPiece(boardStatePort.getState());

            if (boardStatePort.getState().isWhiteShah() || boardStatePort.getState().isBlackShah()) {
                // After the move, the king is still in check – illegal move
                piece.undo(boardStatePort.getState());
                checkAllPiece(boardStatePort.getState());
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
            checkAllPiece(boardStatePort.getState());

            if (boardStatePort.getState().isShah(piece.getColor())) {
                // The move puts the moving player's own king in check – illegal
                piece.undo(boardStatePort.getState());
                checkAllPiece(boardStatePort.getState());
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
            if (cell.getRow() == lastRow && boardStatePort != null) {
                PieceColor color = piece.getColor();
                boolean toPromote = boardStatePort.showPromotionDialog(color, type -> {
                    // Removing a pawn
                    boardStatePort.getState().removePiece(cell);
                    // Adding a new figure
                    Piece promoted = null;
                    switch (type) {
                        case QUEEN -> promoted = new Queen(color, cell);
                        case ROOK -> promoted = new Rook(color, cell);
                        case BISHOP -> promoted = new Bishop(color, cell);
                        case KNIGHT -> promoted = new Knight(color, cell);
                    }
                    if (promoted != null) {
                        boardStatePort.getState().addPiece(promoted);
                        checkAllPiece(boardStatePort.getState());
                        log.info("Pawn of color {} ({}) promoted to piece {}", color, cell, promoted.getType());
                    }
                });

                if (!toPromote) {
                    log.info("Pawn of color {} ({}) did not promote to any piece upon reaching the end of the board", color, cell);
                }
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
