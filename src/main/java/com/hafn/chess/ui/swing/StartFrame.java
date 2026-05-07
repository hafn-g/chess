package com.hafn.chess.ui.swing;

import com.hafn.chess.domain.model.GameConfig;
import com.hafn.chess.domain.model.GameType;
import com.hafn.chess.domain.model.PieceColor;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {
    public StartFrame() {
        setTitle("Chess Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Blitz-round chess game", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Game type selection
        JPanel gameTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel gameTypeLabel = new JLabel("Game type:");
        JComboBox<GameType> gameTypeCombo = new JComboBox<>(GameType.values());
        gameTypePanel.add(gameTypeLabel);
        gameTypePanel.add(gameTypeCombo);
        centerPanel.add(gameTypePanel);

        JPanel rowsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel rowsLabel = new JLabel("Rows (1-8, ⇅):");
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(8, 8, 32, 1));
        rowsPanel.add(rowsLabel);
        rowsPanel.add(rowsSpinner);
        centerPanel.add(rowsPanel);

        JPanel colsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel colsLabel = new JLabel("Columns (A-H, ⇄):");
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(8, 8, 32, 1));
        colsPanel.add(colsLabel);
        colsPanel.add(colsSpinner);
        centerPanel.add(colsPanel);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel timeLabel = new JLabel("Time (sec):");
        JSpinner timeSpinner = new JSpinner(new SpinnerNumberModel(600, 30, 3600, 10));
        timePanel.add(timeLabel);
        timePanel.add(timeSpinner);
        centerPanel.add(timePanel);

        JPanel firstMovePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel firstMoveLabel = new JLabel("First move:");
        String[] colors = {"WHITE", "BLACK"};
        JComboBox<String> firstMoveCombo = new JComboBox<>(colors);
        firstMovePanel.add(firstMoveLabel);
        firstMovePanel.add(firstMoveCombo);
        centerPanel.add(firstMovePanel);

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        playButton.addActionListener(_ -> {
            int rows = (int) rowsSpinner.getValue();
            int cols = (int) colsSpinner.getValue();
            int time = (int) timeSpinner.getValue();
            String firstMove = (String) firstMoveCombo.getSelectedItem();
            PieceColor queue = "BLACK".equalsIgnoreCase(firstMove) ? PieceColor.BLACK : PieceColor.WHITE;
            GameType gameType = (GameType) gameTypeCombo.getSelectedItem();
            GameConfig config = new GameConfig(gameType, rows, cols, time, queue);
            ChessFrame frame = new ChessFrame(config);
            frame.setVisible(true);
            dispose();
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        buttonPanel.add(playButton);
        centerPanel.add(buttonPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
