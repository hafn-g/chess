package com.hafn.chess.app;

import com.hafn.chess.ui.swing.ChessFrame;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            new ChessFrame().setVisible(true);
        });
    }
}
