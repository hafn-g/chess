package com.hafn.chess.ui.swing.panel;

import com.hafn.chess.application.port.in.BoardInputPort;
import com.hafn.chess.application.port.out.BoardRenderPort;
import com.hafn.chess.application.port.out.BoardStatePort;
import com.hafn.chess.application.usecase.BoardController;
import com.hafn.chess.bootstrap.BoardInitializer;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.state.BoardState;
import com.hafn.chess.ui.swing.model.BoardMetrics;
import com.hafn.chess.ui.swing.renderer.BoardRenderer;
import com.hafn.chess.ui.swing.renderer.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;

public class BoardPanel extends JPanel implements BoardStatePort, BoardRenderPort {
    private static final int MIN_SIDE = 400;
    private static final int PREF_SIDE = 600;

    private final BoardState state;
    private final BoardInputPort boardController;
    private final BoardRenderer renderer;

    private final BoardMetrics metrics;

    public BoardPanel() {
        setOpaque(false); // transparent background

        metrics = new BoardMetrics();

        state = BoardInitializer.createDefaultState(PieceColor.BLACK);
        renderer = new BoardRenderer(this);

        boardController = new BoardController(this, renderer);

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
            boardController.clearSelection();
            return;
        }

        boardController.handleClick(row, col);

        repaint();
    }

    public BoardState getState() {
        return this.state;
    }

    public BoardMetrics getMetrics() {
        return metrics;
    }

    public void repaintBoard() {
        repaint();
    }

    /**
     * Shows the dialog for selecting pieces to promote a pawn to
     */
    public void showPromotionDialog(PieceColor color, Consumer<PieceType> onPromotionSelected) {
        // Transformation options
        PieceType[] types = {
                PieceType.QUEEN,
                PieceType.ROOK,
                PieceType.BISHOP,
                PieceType.KNIGHT
        };
        String[] names = {"Queen", "Rook", "Bishop", "Knight"};
        Icon[] icons = new Icon[types.length];
        for (int i = 0; i < types.length; i++) {
            Image image = ImageCache.get(types[i], (color == PieceColor.WHITE ? PieceColor.WHITE : PieceColor.BLACK));
            icons[i] = new ImageIcon(image);
        }
        JPanel panel = new JPanel(new GridLayout(0, types.length, 10, 10));
        ButtonGroup group = new ButtonGroup();
        JToggleButton[] buttons = new JToggleButton[types.length];
        for (int i = 0; i < types.length; i++) {
            buttons[i] = new JToggleButton(names[i], icons[i]);
            buttons[i].setVerticalTextPosition(SwingConstants.BOTTOM);
            buttons[i].setHorizontalTextPosition(SwingConstants.CENTER);
            group.add(buttons[i]);
            panel.add(buttons[i]);
        }
        buttons[0].setSelected(true);
        JLabel label = new JLabel("Choose a piece for promotion:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(panel, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, wrapper, "Promotion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < types.length; i++) {
                if (buttons[i].isSelected()) {
                    onPromotionSelected.accept(types[i]);
                    return;
                }
            }
        }
    }

    /**
     * Shows a simple information dialog with a custom title and message.
     */
    public void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
