package com.hafn.chess.ui.swing;

import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.ui.swing.panel.BoardPanel;
import com.hafn.chess.ui.swing.panel.StatsPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class ChessFrame extends JFrame {
    private JPanel mainPanel;

    public ChessFrame() {
        initUI();
    }

    public ChessFrame(int rows, int cols, int playerTime, PieceColor queue) {
        initUI(rows, cols, playerTime, queue);
    }

    private void initUI() {
        initUI(8, 8, 600, PieceColor.WHITE);
    }

    private void initUI(int rows, int cols, int playerTime, PieceColor queue) {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        setContentPane(mainPanel);

        // Chess board panel
        BoardPanel boardPanel = initBoard(rows, cols, playerTime, queue);
        log.debug("Board initialized: {}", boardPanel);

        // Stats
        JPanel statsPanel = new StatsPanel(boardPanel);
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        log.debug("Statistics initialized: {}", statsPanel);

        pack();
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(null);

        log.info("Game window initialized and shown to user");
    }

    private BoardPanel initBoard(int rows, int cols, int playerTime, PieceColor queue) {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        BoardPanel boardPanel = new BoardPanel(rows, cols, playerTime, queue);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerWrapper.add(boardPanel, gbc);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return boardPanel;
    }
}