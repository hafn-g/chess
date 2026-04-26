package com.hafn.panel;

import com.hafn.data.BoardMetrics;
import com.hafn.data.Cell;
import com.hafn.data.Piece;
import com.hafn.logic.BoardState;
import com.hafn.logic.MoveGenerator;
import com.hafn.ui.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;

import static com.hafn.logic.BoardInitializer.initPieces;

public class BoardPanel extends JPanel {
    private static final int MIN_SIDE = 400;
    private static final int PREF_SIDE = 600;
    private final int ROWS = 8;
    private final int COLS = 8;

    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

    private int hoveredRow = -1;
    private int hoveredCol = -1;
    private int selectedRow = -1;
    private int selectedCol = -1;

    private final BoardState state;

    public BoardPanel() {
        setOpaque(false); // transparent background
        
        BoardMetrics metrics = new BoardMetrics(ROWS, COLS);

        state = new BoardState(metrics);
        initPieces(state);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                clearHover();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    private void updateHover(int mouseX, int mouseY) {
        int padding = 30;
        int side = Math.min(getWidth(), getHeight()) - padding;
        int cellSize = side / state.getMetrics().getRows();

        int boardX = padding;
        int boardY = 0;

        int col = (mouseX - boardX) / cellSize;
        int row = (mouseY - boardY) / cellSize;

        if (row >= 0 && row < state.getMetrics().getRows() && col >= 0 && col < state.getMetrics().getCols()) {
            if (row != hoveredRow || col != hoveredCol) {
                hoveredRow = row;
                hoveredCol = col;
                repaint();
            }
        } else {
            if (hoveredRow != -1 || hoveredCol != -1) {
                hoveredRow = -1;
                hoveredCol = -1;
                repaint();
            }
        }
    }

    private void clearHover() {
        if (hoveredRow != -1 || hoveredCol != -1) {
            hoveredRow = -1;
            hoveredCol = -1;
            repaint();
        }
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        state.clearPossibleMoves();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_SIDE, PREF_SIDE);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(MIN_SIDE, MIN_SIDE);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        int side = Math.min(width, height);
        if (side < MIN_SIDE) side = MIN_SIDE;
        int newX = x + (width - side) / 2;
        int newY = y + (height - side) / 2;
        super.setBounds(newX, newY, side, side);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        state.getMetrics().calcMetrics(getWidth(), getHeight());

        drawBoard(g2d);
        drawCoords(g2d);
    }

    private void drawBoard(Graphics2D g2d) {
        for (int row = 0; row < state.getMetrics().getRows(); row++) {
            for (int col = 0; col < state.getMetrics().getCols(); col++) {

                Cell cell = state.getCell(row, col);

                boolean hovered = (row == hoveredRow && col == hoveredCol);
                boolean selected = (row == selectedRow && col == selectedCol);
                boolean isMove = state.getPossibleMoves().contains(new Point(row, col));

                Color color = cell.isLight() ? lightColor : darkColor;
                if (isMove) {
                    if (state.getPiece(cell) != null) {
                        color = new Color(255, 0, 0, 100);
                    } else {
                        color = new Color(0, 255, 0, 100);
                    }
                } else if (selected) {
                    color = color.darker();
                } else if (hovered) {
                    color = color.brighter();
                }

                int x = state.getMetrics().getBoardX() + col * state.getMetrics().getCellSize();
                int y = state.getMetrics().getBoardY() + row * state.getMetrics().getCellSize();

                g2d.setColor(color);
                g2d.fillRect(x, y, state.getMetrics().getCellSize(), state.getMetrics().getCellSize());

                // draw piece
                if (state.getPiece(cell) != null) {
                    Image img = getImage(state.getPiece(cell));
                    g2d.drawImage(img, x, y, state.getMetrics().getCellSize(), state.getMetrics().getCellSize(), null);
                }

                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, state.getMetrics().getCellSize(), state.getMetrics().getCellSize());
            }
        }
    }

    private Image getImage(Piece piece) {
        String key = piece.getColor().name().toLowerCase() + "_" +
                piece.getType().name().toLowerCase();
        return ImageCache.get(key);
    }

    private void drawCoords(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, state.getMetrics().getCellSize() / 5));

        FontMetrics fm = g2d.getFontMetrics();

        drawLetters(g2d, fm);
        drawNumbers(g2d, fm);
    }

    private void drawLetters(Graphics2D g2d, FontMetrics fm) {
        for (int col = 0; col < state.getMetrics().getCols(); col++) {
            String letter = String.valueOf((char) ('A' + col));

            int textWidth = fm.stringWidth(letter);
            int x = state.getMetrics().getBoardX() + col * state.getMetrics().getCellSize() + (state.getMetrics().getCellSize() - textWidth) / 2;
            int y = state.getMetrics().getBoardY() + state.getMetrics().getSide() + fm.getAscent();

            g2d.drawString(letter, x, y);
        }
    }

    private void drawNumbers(Graphics2D g2d, FontMetrics fm) {
        for (int row = 0; row < state.getMetrics().getRows(); row++) {
            String number = String.valueOf(state.getMetrics().getRows() - row);

            int textHeight = fm.getAscent();
            int x = state.getMetrics().getPadding() / 3;
            int y = state.getMetrics().getBoardY() + row * state.getMetrics().getCellSize() + (state.getMetrics().getCellSize() + textHeight) / 2;

            g2d.drawString(number, x, y);
        }
    }

    private void handleClick(int mouseX, int mouseY) {
        int col = (mouseX - state.getMetrics().getBoardX()) / state.getMetrics().getCellSize();
        int row = (mouseY - state.getMetrics().getBoardY()) / state.getMetrics().getCellSize();

        // outside board
        if (row < 0 || row >= state.getMetrics().getRows() || col < 0 || col >= state.getMetrics().getCols()) {
            clearSelection();
            return;
        }

        Cell clicked = state.getCell(row, col);

        // nothing selected yet
        if (selectedRow == -1) {
            if (state.getPiece(clicked) != null) {
                selectedRow = row;
                selectedCol = col;

                state.setPossibleMoves(new HashSet<>(MoveGenerator.generate(state, row, col)));
            }
        }
        // already selected
        else {
            // same cell -> unselect
            if (row == selectedRow && col == selectedCol) {
                clearSelection();
            } else if (state.getPiece(clicked) != null) {
                selectedRow = row;
                selectedCol = col;

                state.setPossibleMoves(new HashSet<>(MoveGenerator.generate(state, row, col)));
            } else {
                clearSelection();
            }
        }

        repaint();
    }
}
