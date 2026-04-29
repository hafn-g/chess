
package com.hafn.chess.ui.swing.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;


public class StatsPanel extends JPanel {
    private final BoardPanel boardPanel;
    private final JLabel turnLabel;
    private final JLabel whiteTimeLabel;
    private final JLabel blackTimeLabel;
    private final JLabel gameTimeLabel;

    private final Timer timer;

    public StatsPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 0, 16, 0));

        Font statsFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);

        turnLabel = new JLabel("Turn: " + getTurnText());
        turnLabel.setFont(statsFont);
        turnLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(turnLabel);

        whiteTimeLabel = new JLabel("White time: " + formatTime(boardPanel.getState().getWhiteTime()));
        whiteTimeLabel.setFont(statsFont);
        whiteTimeLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(whiteTimeLabel);

        blackTimeLabel = new JLabel("Black time: " + formatTime(boardPanel.getState().getBlackTime()));
        blackTimeLabel.setFont(statsFont);
        blackTimeLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(blackTimeLabel);

        gameTimeLabel = new JLabel("Game time: 00:00");
        gameTimeLabel.setFont(statsFont);
        gameTimeLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(gameTimeLabel);

        timer = new Timer(500, _ -> updateStats());
        timer.start();
        updateStats();
    }

    private String getTurnText() {
        if (boardPanel.getState().getQueue() == null) return "-";
        return boardPanel.getState().getQueue().name().equals("WHITE") ? "White" : "Black";
    }

    private void updateStats() {
        turnLabel.setText("Turn: " + getTurnText());
        whiteTimeLabel.setText("White time: " + formatTime(boardPanel.getState().getWhiteTime()));
        blackTimeLabel.setText("Black time: " + formatTime(boardPanel.getState().getBlackTime()));

        LocalDateTime start = boardPanel.getState().getStartGameDateTime();
        int elapsed = 0;
        if (start != null) {
            elapsed = (int) Duration.between(start, LocalDateTime.now()).getSeconds();
        }
        gameTimeLabel.setText("Game time: " + formatTime(elapsed));

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
