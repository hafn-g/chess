package com.hafn.chess.application.usecase;

import com.hafn.chess.application.port.in.BoardInputPort;
import com.hafn.chess.application.port.out.BoardStatePort;
import com.hafn.chess.application.port.out.SelectionPort;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.*;

import static com.hafn.chess.domain.service.CheckRule.checkAllPiece;

public class BoardController implements BoardInputPort {
    private final BoardStatePort boardStatePort;
    private final SelectionPort selectionPort;

    public BoardController(BoardStatePort boardStatePort, SelectionPort selectionPort) {
        this.boardStatePort = boardStatePort;
        this.selectionPort = selectionPort;
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

        Cell clicked = boardStatePort.getState().getCell(row, col);

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
        // Check if the current player's king is in check
        if (boardStatePort.getState().isWhiteShah() || boardStatePort.getState().isBlackShah()) {
            // King is in check – try to move out of check
            piece.execute(boardStatePort.getState(), clicked);
            checkAllPiece(boardStatePort.getState());

            if (boardStatePort.getState().isWhiteShah() || boardStatePort.getState().isBlackShah()) {
                // After the move, the king is still in check – illegal move
                piece.undo(boardStatePort.getState());
                checkAllPiece(boardStatePort.getState());
                System.out.println("Move failed: King would still be in check.");
                boardStatePort.showInfoDialog("Move failed", "King would still be in check");
            } else {
                // Move successfully resolved the check
                handlePromotionIfNeeded(piece, clicked);
                boardStatePort.getState().nextQueue();
                System.out.println("Move successful: King is no longer in check. " + fromCell.getName() + " -> " + clicked.getName());
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
                System.out.println("Move failed: Would put own king in check.");
                boardStatePort.showInfoDialog("Move failed", "Would put own king in check");
            } else {
                // Legal move
                handlePromotionIfNeeded(piece, clicked);
                boardStatePort.getState().nextQueue();
                System.out.println("Move successful: " + fromCell.getName() + " -> " + clicked.getName());
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
                boardStatePort.showPromotionDialog(color, type -> {
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
                    }
                });
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
            }
        } else { // already selected
            // same cell -> unselect
            if (selectionPort.getSelectedCell() == clicked) {
                clearSelection();
            } else if (boardStatePort.getState().getPiece(clicked) != null) {
                selectionPort.setSelectedCell(clicked);
                boardStatePort.getState().setClickedCell(clicked);
            } else {
                clearSelection();
            }
        }
    }
}
