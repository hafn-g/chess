package com.hafn.chess.ui.swing.panel;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Slf4j
public class StatsPanel extends JPanel {
    private final BoardPanel boardPanel;
    private final JLabel turnLabel;
    private final JLabel whiteTimeLabel;
    private final JLabel blackTimeLabel;
    private final JLabel gameTimeLabel;
    private final JButton pauseButton;

    private final Timer timer;

    public StatsPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        Font statsFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);

        turnLabel = new JLabel("Turn: " + getTurnText());
        turnLabel.setFont(statsFont);
        turnLabel.setAlignmentX(CENTER_ALIGNMENT);
        textPanel.add(turnLabel);

        whiteTimeLabel = new JLabel("White time: " + formatTime(boardPanel.getState().getWhiteTime()));
        whiteTimeLabel.setFont(statsFont);
        whiteTimeLabel.setAlignmentX(CENTER_ALIGNMENT);
        textPanel.add(whiteTimeLabel);

        blackTimeLabel = new JLabel("Black time: " + formatTime(boardPanel.getState().getBlackTime()));
        blackTimeLabel.setFont(statsFont);
        blackTimeLabel.setAlignmentX(CENTER_ALIGNMENT);
        textPanel.add(blackTimeLabel);

        gameTimeLabel = new JLabel("Game time: 00:00");
        gameTimeLabel.setFont(statsFont);
        gameTimeLabel.setAlignmentX(CENTER_ALIGNMENT);
        textPanel.add(gameTimeLabel);

        add(textPanel, BorderLayout.CENTER);

        pauseButton = new JButton("‖");
        pauseButton.setFont(statsFont);
        pauseButton.addActionListener(_ -> togglePause());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(pauseButton, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.EAST);

        timer = new Timer(500, _ -> updateStats());
        timer.start();
        updateStats();
    }

    private void togglePause() {
        boolean isPaused = boardPanel.getState().isPauseGame();
        boardPanel.getState().setPauseGame(!isPaused);
        pauseButton.setText(isPaused ? "‖" : "▶");
    }

    private String getTurnText() {
        if (boardPanel.getState().getQueue() == null) return "-";
        return boardPanel.getState().getQueue().name().equals("WHITE") ? "White" : "Black";
    }

    private void updateStats() {
        turnLabel.setText("Turn: " + getTurnText());
        whiteTimeLabel.setText("White time: " + formatTime(boardPanel.getState().getWhiteTime()));
        blackTimeLabel.setText("Black time: " + formatTime(boardPanel.getState().getBlackTime()));

        int elapsed = boardPanel.getState().getGameTime();
        gameTimeLabel.setText("Game time: " + formatTime(elapsed));

        log.trace(
                "Current turn: {}, Black time: {}, White time: {}, Game time: {}",
                boardPanel.getState().getQueue(),
                boardPanel.getState().getWhiteTime(),
                boardPanel.getState().getBlackTime(),
                formatTime(elapsed)
        );

        if (boardPanel.getState().getWhiteTime() <= 0 || boardPanel.getState().getBlackTime() <= 0) {
            boardPanel.showInfoDialog("Game Finished", "Time is up!");
            timer.stop();
            boardPanel.getState().stopGame();
        }
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
