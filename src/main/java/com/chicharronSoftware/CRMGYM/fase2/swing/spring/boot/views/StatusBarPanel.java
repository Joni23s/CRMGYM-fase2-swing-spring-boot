package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views;

import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * [MEJORA EDITORIAL VINTAGE] StatusBarPanel adaptada al tema Tinta & Papel Envejecido.
 */
@Component
public class StatusBarPanel extends JPanel {

    private static final Color TEXT_COLOR = Color.decode("#A89885");
    private static final Color BORDER_COLOR = Color.decode("#3B3127");

    private JLabel lblConnection;
    private JComponent greenDot;
    private Color currentStatusColor = Color.decode("#8C5E12"); // Mostaza inicial

    public StatusBarPanel() {
        initComponents();
    }

    private void initComponents() {
        setBackground(Color.decode("#211B15"));
        setLayout(new MigLayout("ins 4 14 4 14, aligny center, fillx"));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JPanel connectionPanel = new JPanel(new MigLayout("ins 0, gapx 8, aligny center"));
        connectionPanel.setOpaque(false);

        greenDot = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(currentStatusColor);
                int size = Math.min(getWidth(), getHeight());
                g2d.fillOval(0, (getHeight() - size) / 2, size, size);
                g2d.dispose();
            }
        };
        greenDot.setPreferredSize(new Dimension(8, 8));

        lblConnection = new JLabel("CONECTADO: crmgym_fase2");
        lblConnection.setFont(new Font("Courier Prime", Font.BOLD, 11));
        lblConnection.setForeground(currentStatusColor);

        connectionPanel.add(greenDot);
        connectionPanel.add(lblConnection);

        add(connectionPanel, "cell 0 0, alignx left");

        JLabel lblVersion = new JLabel("VERSIÓN 2.0.0 | Jun 24, 2026");
        lblVersion.setFont(new Font("Courier Prime", Font.BOLD, 11));
        lblVersion.setForeground(TEXT_COLOR);

        add(lblVersion, "cell 1 0, pushx, al right");
    }

    public void setConnectionStatus(boolean connected, String databaseName) {
        if (connected) {
            currentStatusColor = Color.decode("#8C5E12"); // Mostaza legible
            lblConnection.setText("CONECTADO: " + databaseName);
            lblConnection.setForeground(currentStatusColor);
        } else {
            currentStatusColor = Color.decode("#8C2320"); // Sangre de Toro
            lblConnection.setText("DESCONECTADO: Sin base de datos");
            lblConnection.setForeground(currentStatusColor);
        }
        greenDot.repaint();
    }
}
