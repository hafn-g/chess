package com.hafn.chess.controller;

import com.hafn.chess.model.Cell;
import com.hafn.chess.model.piece.Piece;
import com.hafn.chess.panel.BoardPanel;

public class BoardController {
    private final BoardPanel boardPanel;

    public BoardController(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    /*
        Works only with cells
     */
    public void handleClick(int row, int col) {
        Cell clicked = boardPanel.getState().getCell(row, col);

        boolean isMove = boardPanel.getState().getPossibleMoves().contains(clicked);
        if (isMove) {
            boardPanel.getState().getPiece(boardPanel.getState().getClickedCell()).execute(boardPanel.getState(), clicked);
            boardPanel.getRenderer().clearSelection();
        } else {
            toSelected(clicked);
        }
    }

    private void toSelected(Cell clicked) {
        Piece clickedCellPiece = boardPanel.getState().getPiece(clicked);

        // nothing selected yet
        if (boardPanel.getRenderer().getCellSelected() == null) {
            if (boardPanel.getState().getPiece(clicked) != null) {
                boardPanel.getRenderer().setCellSelected(clicked);
                boardPanel.getState().setPossibleMoves(clickedCellPiece.possibleMoves(boardPanel.getState(), clicked));
                boardPanel.getState().setClickedCell(clicked);
            }
        } else { // already selected
            // same cell -> unselect
            if (boardPanel.getRenderer().getCellSelected() == clicked) {
                boardPanel.getRenderer().clearSelection();
            } else if (boardPanel.getState().getPiece(clicked) != null) {
                boardPanel.getRenderer().setCellSelected(clicked);
                boardPanel.getState().setPossibleMoves(clickedCellPiece.possibleMoves(boardPanel.getState(), clicked));
                boardPanel.getState().setClickedCell(clicked);
            } else {
                boardPanel.getRenderer().clearSelection();
            }
        }
    }
}
