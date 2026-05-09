package com.hafn.chess.ui.swing;

import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.network.MyClient;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class ClientFrame extends JFrame {
    private final JTextField ipField;
    private final JTextField portField;
    private final JTextField nicknameField;
    private final JButton joinButton;
    private final JComboBox<String> colorCombo;

    public ClientFrame(OnlineModeFrame parent) {
        setTitle("Client Mode");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel ipLabel = new JLabel("IP:");
        ipField = new JTextField("127.0.0.1", 10);
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        panel.add(ipPanel);

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField("2468", 5);
        portPanel.add(portLabel);
        portPanel.add(portField);
        panel.add(portPanel);

        JPanel nickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nickLabel = new JLabel("Nickname:");
        nicknameField = new JTextField(16);
        nickPanel.add(nickLabel);
        nickPanel.add(nicknameField);
        panel.add(nickPanel);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("Color:");
        colorCombo = new JComboBox<>(new String[]{"WHITE", "BLACK"});
        colorPanel.add(colorLabel);
        colorPanel.add(colorCombo);
        panel.add(colorPanel);

        joinButton = new JButton("Join the game");
        joinButton.setEnabled(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(joinButton);
        panel.add(buttonPanel);

        setContentPane(panel);

        ipField.getDocument().addDocumentListener(new SimpleListener(this::validateFields));
        portField.getDocument().addDocumentListener(new SimpleListener(this::validateFields));
        nicknameField.getDocument().addDocumentListener(new SimpleListener(this::validateFields));

        joinButton.addActionListener(_ -> onJoinGame());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parent.setVisible(true);
                resetFields();
            }
        });
    }

    private void validateFields() {
        String ip = ipField.getText().trim();
        String port = portField.getText().trim();
        String nick = nicknameField.getText().trim();
        boolean valid = isValidIp(ip) && isValidPort(port) && isValidNickname(nick);
        joinButton.setEnabled(valid);
    }

    private boolean isValidIp(String ip) {
        if ("localhost".equals(ip)) return true;
        String ipRegex = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
        if (!Pattern.matches(ipRegex, ip)) return false;
        String[] parts = ip.split("\\.");
        for (String part : parts) {
            int n = Integer.parseInt(part);
            if (n < 0 || n > 255) return false;
        }
        return true;
    }

    private boolean isValidPort(String port) {
        try {
            int p = Integer.parseInt(port);
            return p >= 1 && p <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidNickname(String nick) {
        return nick.matches("[A-Za-z0-9_-]{3,64}");
    }

    private void onJoinGame() {
        joinButton.setEnabled(false);
        try {
            String colorStr = (String) colorCombo.getSelectedItem();
            PieceColor color = PieceColor.valueOf(colorStr);
            String nickname = nicknameField.getText().trim();
            log.info(
                    "Attempting to connect {}:{} with nickname {} ({})",
                    ipField.getText(),
                    portField.getText(),
                    nickname,
                    color
            );
            new MyClient(
                    ipField.getText(),
                    Integer.parseInt(portField.getText()),
                    nickname,
                    color,
                    this,
                    new MyClient.MyClientListener() {
                        @Override
                        public void onError(String message) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(ClientFrame.this, message, "Connection error", JOptionPane.ERROR_MESSAGE);
                                resetFields();
                            });
                        }

                        @Override
                        public void onGameStart() {
                        }
                    }
            );

            joinButton.setText("Success! Waiting for launch");
            setFieldsEnabled(false);

            log.info(
                    "Successfully connected {}:{} with nickname {} ({})",
                    ipField.getText(),
                    portField.getText(),
                    nickname,
                    color
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect: " + e.getMessage(), "Connection error", JOptionPane.ERROR_MESSAGE);
            joinButton.setText("Join the game");
            setFieldsEnabled(true);
            joinButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unknown error: " + e.getMessage(), "Connection error", JOptionPane.ERROR_MESSAGE);
            joinButton.setText("Join the game");
            setFieldsEnabled(true);
            joinButton.setEnabled(true);
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        ipField.setEnabled(enabled);
        portField.setEnabled(enabled);
        nicknameField.setEnabled(enabled);
        colorCombo.setEnabled(enabled);
    }

    private void resetFields() {
        setFieldsEnabled(true);
        joinButton.setText("Join the game");
        joinButton.setEnabled(false);
        nicknameField.setText("");
    }

    private static class SimpleListener implements javax.swing.event.DocumentListener {
        private final Runnable cb;
        SimpleListener(Runnable cb) { this.cb = cb; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { cb.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { cb.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { cb.run(); }
    }
}
