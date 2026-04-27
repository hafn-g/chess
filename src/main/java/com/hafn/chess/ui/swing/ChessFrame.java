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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Stats
        JPanel statsPanel = new StatsPanel();
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Chess board panel
        initBoard();

        pack();
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(null);
    }

    private void initBoard() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        BoardPanel boardPanel = new BoardPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerWrapper.add(boardPanel, gbc);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
    }
}