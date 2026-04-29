package com.hafn.chess.bootstrap;

import com.hafn.chess.ui.swing.ChessFrame;

import javax.swing.*;

public class Main {
    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChessFrame().setVisible(true);
        });
    }
}
