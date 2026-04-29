package com.hafn.chess.ui.swing;

import com.hafn.chess.ui.swing.panel.BoardPanel;
import com.hafn.chess.ui.swing.panel.StatsPanel;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {
    private JPanel mainPanel;

    public ChessFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        setContentPane(mainPanel);

        // Chess board panel
        BoardPanel boardPanel = initBoard();

        // Stats
        JPanel statsPanel = new StatsPanel(boardPanel);
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        pack();
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(null);
    }

    private BoardPanel initBoard() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        BoardPanel boardPanel = new BoardPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerWrapper.add(boardPanel, gbc);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return boardPanel;
    }
}