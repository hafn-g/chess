package com.hafn.chess.bootstrap;

import com.hafn.chess.ui.swing.StartFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartFrame().setVisible(true));
    }
}
