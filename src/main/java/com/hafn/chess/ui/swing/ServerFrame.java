package com.hafn.chess.ui.swing;

import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.network.ClientHandler;
import com.hafn.chess.network.MyServer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerFrame extends JFrame {
    private final JButton startServerButton;
    private final JButton launchGameButton;
    private final JTextField portField;
    private final JLabel ipLabel;
    private MyServer server;
    private final DefaultListModel<String> playersModel = new DefaultListModel<>();

    public ServerFrame(OnlineModeFrame parent) {
        setTitle("Server Mode");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipLabel = new JLabel("IP: ...");
        ipLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        ipPanel.add(ipLabel);
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField("2468", 6);
        ipPanel.add(Box.createHorizontalStrut(20));
        ipPanel.add(portLabel);
        ipPanel.add(portField);
        panel.add(ipPanel, BorderLayout.NORTH);

        JPanel playersPanel = new JPanel(new BorderLayout());
        playersPanel.setBorder(BorderFactory.createTitledBorder("Players"));
        JList<String> playersList = new JList<>(playersModel);
        playersList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(playersList);
        playersPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(playersPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        startServerButton = new JButton("Start the server");
        launchGameButton = new JButton("Launch the game");
        launchGameButton.setEnabled(false);
        buttonPanel.add(startServerButton);
        buttonPanel.add(launchGameButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);

        startServerButton.addActionListener(_ -> onStartServer());
        launchGameButton.addActionListener(_ -> onLaunchGame());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parent.setVisible(true);
            }
        });

        new Thread(this::fetchPublicIp).start();
    }

    private void fetchPublicIp() {
        String[] urls = {
                "https://api.ipify.org",
                "https://ifconfig.me/ip",
                "https://icanhazip.com"
        };
        String ip = null;
        for (String url : urls) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
                ip = in.readLine();
                if (ip != null && !ip.isEmpty()) break;
            } catch (IOException ignored) {}
        }
        final String ipFinal = ip;
        SwingUtilities.invokeLater(() -> {
            if (ipFinal != null && !ipFinal.isEmpty()) {
                ipLabel.setText("IP: " + ipFinal);
            } else {
                ipLabel.setText("IP: [error]");
                JOptionPane.showMessageDialog(this, "Failed to get public IP address.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void onStartServer() {
        startServerButton.setEnabled(false);
        new Thread(() -> {
            try {
                int port = Integer.parseInt(portField.getText());
                server = new MyServer();
                getPlayers();
                SwingUtilities.invokeLater(() -> {
                    launchGameButton.setEnabled(true);
                    startServerButton.setEnabled(false);
                    portField.setEnabled(false);
                });
                server.start(port);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Failed to start server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    startServerButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void onLaunchGame() {
        launchGameButton.setEnabled(false);
        try {
            server.startGame();
        } catch (RuntimeException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Failed to start server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                startServerButton.setEnabled(false);
                launchGameButton.setEnabled(true);
            });
        }
    }

    private void getPlayers() {
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            Map<PieceColor, ClientHandler> waitingPlayers = server.getWaitingPlayers();
            SwingUtilities.invokeLater(() -> {
                playersModel.clear();
                for (Map.Entry<PieceColor, ClientHandler> entry : waitingPlayers.entrySet()) {
                    String nick = entry.getValue().getPlayer().getNickname();
                    PieceColor color = entry.getKey();
                    playersModel.addElement(nick + " (" + color + ")");
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
}
