package com.hafn.chess.panel;

import com.hafn.chess.controller.BoardController;
import com.hafn.chess.logic.BoardState;
import com.hafn.chess.model.BoardMetrics;
import com.hafn.chess.ui.BoardRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class BoardPanel extends JPanel {
    private static final int MIN_SIDE = 400;
    private static final int PREF_SIDE = 600;

    private final BoardState state;
    private final BoardController boardController;
    private final BoardRenderer renderer;

    private final BoardMetrics metrics;

    public BoardPanel() {
        setOpaque(false); // transparent background

        metrics = new BoardMetrics();

        state = new BoardState(8, 8);
        renderer = new BoardRenderer(this);

        boardController = new BoardController(this);

        addListener();
    }

    private void addListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                renderer.updateHover(e.getX(), e.getY(), getWidth(), getHeight());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                renderer.clearHover();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
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

        metrics.calcMetrics(getWidth(), getHeight());

        renderer.drawBoard(g2d);
        renderer.drawCoords(g2d);
    }

    /*
        Converts pixels to rows and columns
     */
    public void handleClick(int mouseX, int mouseY) {
        int col = (mouseX - metrics.getBoardX()) / metrics.getCellSize();
        int row = (mouseY - metrics.getBoardY()) / metrics.getCellSize();

        // outside board
        if (!state.inBounds(row, col)) {
            renderer.clearSelection();
            return;
        }

        boardController.handleClick(row, col);

        repaint();
    }

    public BoardState getState() {
        return this.state;
    }

    public BoardRenderer getRenderer() {
        return this.renderer;
    }

    public BoardMetrics getMetrics() {
        return metrics;
    }

    public void toRepaint() {
        repaint();
    }
}
