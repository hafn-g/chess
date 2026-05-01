package com.hafn.chess.ui.swing.renderer;

import com.hafn.chess.application.port.out.BoardRenderPort;
import com.hafn.chess.application.port.out.SelectionPort;
import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.piece.Piece;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.awt.*;

@ToString
@EqualsAndHashCode
public class BoardRenderer implements SelectionPort {
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

    private Cell cellHovered;
    private Cell cellSelected;

    private final BoardRenderPort boardRenderPort;

    public BoardRenderer(BoardRenderPort boardRenderPort) {
        this.boardRenderPort = boardRenderPort;
    }

    public void drawBoard(Graphics2D g2d) {
        for (int row = 0; row < boardRenderPort.getMetrics().getRows(); row++) {
            for (int col = 0; col < boardRenderPort.getMetrics().getCols(); col++) {
                Cell cell = boardRenderPort.getState().getCell(row, col);
                Piece piece = boardRenderPort.getState().getPiece(cell);

                int x = boardRenderPort.getMetrics().getBoardX() + col * boardRenderPort.getMetrics().getCellSize();
                int y = boardRenderPort.getMetrics().getBoardY() + row * boardRenderPort.getMetrics().getCellSize();

                g2d.setColor(getDrawBoardColor(cell));
                g2d.fillRect(x, y, boardRenderPort.getMetrics().getCellSize(), boardRenderPort.getMetrics().getCellSize());

                // draw piece
                if (piece != null) {
                    g2d.drawImage(ImageCache.get(piece), x, y, boardRenderPort.getMetrics().getCellSize(), boardRenderPort.getMetrics().getCellSize(), null);
                }

                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, boardRenderPort.getMetrics().getCellSize(), boardRenderPort.getMetrics().getCellSize());
            }
        }
    }

    private Color getDrawBoardColor(Cell cell) {
        Piece piece = boardRenderPort.getState().getPiece(cell);
        if (piece != null) {
            if (boardRenderPort.getState().isBlackShah()) {
                if (piece.getType().equals(PieceType.KING) && piece.getColor().equals(PieceColor.BLACK)) {
                    return new Color(108, 0, 0, 100);
                }
            }
            if (boardRenderPort.getState().isWhiteShah()) {
                if (piece.getType().equals(PieceType.KING) && piece.getColor().equals(PieceColor.WHITE)) {
                    return new Color(108, 0, 0, 100);
                }
            }
        }
        boolean hovered = cellHovered == cell;
        boolean selected = cellSelected == cell;

        Piece pieceClicked = boardRenderPort.getState().getPiece(boardRenderPort.getState().getClickedCell());
        boolean isMove = false;
        if (pieceClicked != null) {
            isMove = pieceClicked.getPossibleMoves().contains(cell);
        }

        Color color = cell.isLight() ? lightColor : darkColor;
        if (isMove) {
            if (piece != null) {
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
        g2d.setFont(new Font("Arial", Font.BOLD, boardRenderPort.getMetrics().getCellSize() / 5));

        FontMetrics fm = g2d.getFontMetrics();

        drawLetters(g2d, fm);
        drawNumbers(g2d, fm);
    }

    private void drawLetters(Graphics2D g2d, FontMetrics fm) {
        for (int col = 0; col < boardRenderPort.getMetrics().getCols(); col++) {
            String letter = String.valueOf((char) ('A' + col));

            int textWidth = fm.stringWidth(letter);
            int x = boardRenderPort.getMetrics().getBoardX() + col * boardRenderPort.getMetrics().getCellSize() + (boardRenderPort.getMetrics().getCellSize() - textWidth) / 2;
            int y = boardRenderPort.getMetrics().getBoardY() + boardRenderPort.getMetrics().getSide() + fm.getAscent();

            g2d.drawString(letter, x, y);
        }
    }

    private void drawNumbers(Graphics2D g2d, FontMetrics fm) {
        for (int row = 0; row < boardRenderPort.getMetrics().getRows(); row++) {
            String number = String.valueOf(boardRenderPort.getMetrics().getRows() - row);

            int textHeight = fm.getAscent();
            int x = boardRenderPort.getMetrics().getPadding() / 3;
            int y = boardRenderPort.getMetrics().getBoardY() + row * boardRenderPort.getMetrics().getCellSize() + (boardRenderPort.getMetrics().getCellSize() + textHeight) / 2;

            g2d.drawString(number, x, y);
        }
    }

    public void updateHover(int mouseX, int mouseY, int width, int height) {
        int padding = 30;
        int side = Math.min(width, height) - padding;
        int cellSize = side / boardRenderPort.getMetrics().getRows();

        int boardY = 0;

        int col = (mouseX - padding) / cellSize;
        int row = (mouseY - boardY) / cellSize;

        if (row >= 0 && row < boardRenderPort.getMetrics().getRows() && col >= 0 && col < boardRenderPort.getMetrics().getCols()) {
            Cell newCellHovered = boardRenderPort.getState().getCell(row, col);
            if (newCellHovered != cellHovered) {
                cellHovered = newCellHovered;
            }
        } else {
            if (cellHovered != null) {
                cellHovered = null;
            }
        }

        boardRenderPort.repaintBoard();
    }

    public void clearHover() {
        if (cellHovered != null) {
            cellHovered = null;
            boardRenderPort.repaintBoard();
        }
    }

    @Override
    public void clearSelectedCell() {
        cellSelected = null;
    }

    @Override
    public Cell getSelectedCell() {
        return cellSelected;
    }

    @Override
    public void setSelectedCell(Cell cellSelected) {
        this.cellSelected = cellSelected;
    }
}
