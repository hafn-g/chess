package com.hafn.chess;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            new ChessGui().setVisible(true);
        });
    }
}
