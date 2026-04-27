package com.hafn.chess.ui;

import com.hafn.chess.model.Cell;
import com.hafn.chess.model.piece.Piece;
import com.hafn.chess.panel.BoardPanel;

import java.awt.*;

public class BoardRenderer {
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

    private Cell cellHovered;
    private Cell cellSelected;

    private final BoardPanel boardPanel;

    public BoardRenderer(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void drawBoard(Graphics2D g2d) {
        for (int row = 0; row < boardPanel.getState().getMetrics().getRows(); row++) {
            for (int col = 0; col < boardPanel.getState().getMetrics().getCols(); col++) {
                Cell cell = boardPanel.getState().getCell(row, col);
                Piece piece = boardPanel.getState().getPiece(cell);

                int x = boardPanel.getState().getMetrics().getBoardX() + col * boardPanel.getState().getMetrics().getCellSize();
                int y = boardPanel.getState().getMetrics().getBoardY() + row * boardPanel.getState().getMetrics().getCellSize();

                g2d.setColor(getDrawBoardColor(cell));
                g2d.fillRect(x, y, boardPanel.getState().getMetrics().getCellSize(), boardPanel.getState().getMetrics().getCellSize());

                // draw piece
                if (piece != null) {
                    g2d.drawImage(piece.getImage(), x, y, boardPanel.getState().getMetrics().getCellSize(), boardPanel.getState().getMetrics().getCellSize(), null);
                }

                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, boardPanel.getState().getMetrics().getCellSize(), boardPanel.getState().getMetrics().getCellSize());
            }
        }
    }

    private Color getDrawBoardColor(Cell cell) {
        boolean hovered = cellHovered == cell;
        boolean selected = cellSelected == cell;
        boolean isMove = boardPanel.getState().getPossibleMoves().contains(cell);

        Color color = cell.isLight() ? lightColor : darkColor;
        if (isMove) {
            if (boardPanel.getState().getPiece(cell) != null) {
                color = new Color(255, 0, 0, 100);
            } else {
                color = new Color(0, 255, 0, 100);
            }
        } else if (selected) {
            color = color.darker();
        } else if (hovered) {
            color = color.brighter();
        }

        return color;
    }

    public void drawCoords(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, boardPanel.getState().getMetrics().getCellSize() / 5));

        FontMetrics fm = g2d.getFontMetrics();

        drawLetters(g2d, fm);
        drawNumbers(g2d, fm);
    }

    private void drawLetters(Graphics2D g2d, FontMetrics fm) {
        for (int col = 0; col < boardPanel.getState().getMetrics().getCols(); col++) {
            String letter = String.valueOf((char) ('A' + col));

            int textWidth = fm.stringWidth(letter);
            int x = boardPanel.getState().getMetrics().getBoardX() + col * boardPanel.getState().getMetrics().getCellSize() + (boardPanel.getState().getMetrics().getCellSize() - textWidth) / 2;
            int y = boardPanel.getState().getMetrics().getBoardY() + boardPanel.getState().getMetrics().getSide() + fm.getAscent();

            g2d.drawString(letter, x, y);
        }
    }

    private void drawNumbers(Graphics2D g2d, FontMetrics fm) {
        for (int row = 0; row < boardPanel.getState().getMetrics().getRows(); row++) {
            String number = String.valueOf(boardPanel.getState().getMetrics().getRows() - row);

            int textHeight = fm.getAscent();
            int x = boardPanel.getState().getMetrics().getPadding() / 3;
            int y = boardPanel.getState().getMetrics().getBoardY() + row * boardPanel.getState().getMetrics().getCellSize() + (boardPanel.getState().getMetrics().getCellSize() + textHeight) / 2;

            g2d.drawString(number, x, y);
        }
    }

    public void updateHover(int mouseX, int mouseY, int width, int height) {
        int padding = 30;
        int side = Math.min(width, height) - padding;
        int cellSize = side / boardPanel.getState().getMetrics().getRows();

        int boardX = padding;
        int boardY = 0;

        int col = (mouseX - boardX) / cellSize;
        int row = (mouseY - boardY) / cellSize;

        if (row >= 0 && row < boardPanel.getState().getMetrics().getRows() && col >= 0 && col < boardPanel.getState().getMetrics().getCols()) {
            Cell newCellHovered = boardPanel.getState().getCell(row, col);
            if (newCellHovered != cellHovered) {
                cellHovered = newCellHovered;
            }
        } else {
            if (cellHovered != null) {
                cellHovered = null;
            }
        }

        boardPanel.toRepaint();
    }

    public void clearHover() {
        if (cellHovered != null) {
            cellHovered = null;
            boardPanel.toRepaint();
        }
    }

    public void clearSelection() {
        cellSelected = null;
        boardPanel.getState().clearPossibleMoves();
        boardPanel.toRepaint();
    }

    public Cell getCellSelected() {
        return cellSelected;
    }

    public void setCellSelected(Cell cellSelected) {
        this.cellSelected = cellSelected;
    }
}
