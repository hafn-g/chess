package com.hafn.chess.application;

import com.hafn.chess.application.port.BoardStatePort;
import com.hafn.chess.application.port.SelectionPort;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.piece.Piece;

public class BoardController {
    private final BoardStatePort boardStatePort;
    private final SelectionPort selectionPort;

    public BoardController(BoardStatePort boardStatePort, SelectionPort selectionPort) {
        this.boardStatePort = boardStatePort;
        this.selectionPort = selectionPort;
    }

    /*
        Works only with cells
     */
    public void handleClick(int row, int col) {
        Cell clicked = boardStatePort.getState().getCell(row, col);

        boolean isMove = boardStatePort.getState().getPossibleMoves().contains(clicked);
        if (isMove) {
            boardStatePort.getState().getPiece(boardStatePort.getState().getClickedCell()).execute(boardStatePort.getState(), clicked);
            clearSelection();
        } else {
            toSelected(clicked);
        }
    }

    public void clearSelection() {
        selectionPort.clearSelectedCell();
        boardStatePort.getState().clearPossibleMoves();
        boardStatePort.getState().setClickedCell(null);
    }

    private void toSelected(Cell clicked) {
        Piece clickedCellPiece = boardStatePort.getState().getPiece(clicked);

        // nothing selected yet
        if (selectionPort.getSelectedCell() == null) {
            if (boardStatePort.getState().getPiece(clicked) != null) {
                selectionPort.setSelectedCell(clicked);
                boardStatePort.getState().setPossibleMoves(clickedCellPiece.possibleMoves(boardStatePort.getState(), clicked));
                boardStatePort.getState().setClickedCell(clicked);
            }
        } else { // already selected
            // same cell -> unselect
            if (selectionPort.getSelectedCell() == clicked) {
                clearSelection();
            } else if (boardStatePort.getState().getPiece(clicked) != null) {
                selectionPort.setSelectedCell(clicked);
                boardStatePort.getState().setPossibleMoves(clickedCellPiece.possibleMoves(boardStatePort.getState(), clicked));
                boardStatePort.getState().setClickedCell(clicked);
            } else {
                clearSelection();
            }
        }
    }
}
