package com.hafn.chess.controller;

import com.hafn.chess.model.Cell;
import com.hafn.chess.model.piece.Piece;
import com.hafn.chess.panel.BoardPanel;

public class BoardController {
    private final BoardPanel boardPanel;

    public BoardController(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void handleClick(int mouseX, int mouseY) {
        int col = (mouseX - boardPanel.getState().getMetrics().getBoardX()) / boardPanel.getState().getMetrics().getCellSize();
        int row = (mouseY - boardPanel.getState().getMetrics().getBoardY()) / boardPanel.getState().getMetrics().getCellSize();

        // outside board
        if (row < 0 || row >= boardPanel.getState().getMetrics().getRows() || col < 0 || col >= boardPanel.getState().getMetrics().getCols()) {
            boardPanel.getRenderer().clearSelection();
            return;
        }

        Cell clicked = boardPanel.getState().getCell(row, col);

        boolean isMove = boardPanel.getState().getPossibleMoves().contains(clicked);
        if (isMove) {
            boardPanel.getState().getPiece(boardPanel.getState().getClickedCell()).execute(boardPanel.getState(), clicked);
            boardPanel.getRenderer().clearSelection();
        } else {
            toSelected(clicked);
        }

        boardPanel.toRepaint();
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
