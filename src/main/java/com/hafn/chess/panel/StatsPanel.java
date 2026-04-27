package com.hafn.chess.panel;

import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {
    public StatsPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(new Color(240, 240, 240));
        add(new JLabel("Turn: White | Score: 0-0"));
    }
}
