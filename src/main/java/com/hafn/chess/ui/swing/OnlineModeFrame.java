package com.hafn.chess.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OnlineModeFrame extends JFrame {
    public OnlineModeFrame() {
        setTitle("Online Mode");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton serverButton = new JButton("Start as server");
        JButton clientButton = new JButton("Start as client");
        serverButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(serverButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(clientButton);

        setContentPane(panel);

        serverButton.addActionListener(_ -> {
            ServerFrame serverFrame = new ServerFrame(this);
            serverFrame.setVisible(true);
            setVisible(false);
        });
        clientButton.addActionListener(_ -> {
            ClientFrame clientFrame = new ClientFrame(this);
            clientFrame.setVisible(true);
            setVisible(false);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                new StartFrame().setVisible(true);
            }
        });
    }
}

