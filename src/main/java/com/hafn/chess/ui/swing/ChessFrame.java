package com.hafn.chess.ui.swing;

import com.hafn.chess.domain.model.GameConfig;
import com.hafn.chess.ui.swing.panel.BoardPanel;
import com.hafn.chess.ui.swing.panel.StatsPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class ChessFrame extends JFrame {
    private JPanel mainPanel;

    public ChessFrame(GameConfig config) {
        initUI(config);
    }

    private void initUI(GameConfig config) {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        setContentPane(mainPanel);

        // Chess board panel
        BoardPanel boardPanel = initBoard(config);
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

    private BoardPanel initBoard(GameConfig config) {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        BoardPanel boardPanel = new BoardPanel(config);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerWrapper.add(boardPanel, gbc);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return boardPanel;
    }
}