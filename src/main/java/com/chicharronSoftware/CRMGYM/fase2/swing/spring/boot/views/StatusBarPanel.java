package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views;

import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class StatusBarPanel extends JPanel {

    private static final Color TEXT_COLOR = Color.decode("#94a3b8"); // Slate 400
    private static final Color BORDER_COLOR = Color.decode("#1e293b"); // Slate 800

    private JLabel lblConnection;
    private JComponent greenDot;
    private Color currentStatusColor = Color.decode("#10b981"); // Verde esmeralda inicial

    public StatusBarPanel() {
        initComponents();
    }

    private void initComponents() {
        // Fondo sutilmente oscuro para la barra de estado
        setBackground(Color.decode("#090d16"));
        
        // MigLayout: padding vertical 5, horizontal 15, centrado verticalmente
        setLayout(new MigLayout("ins 5 15 5 15, aligny center, fillx"));

        // Borde superior para separar de la sección de contenido
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        // --- Lado Izquierdo: Círculo brillante y texto de conexión ---
        JPanel connectionPanel = new JPanel(new MigLayout("ins 0, gapx 8, aligny center"));
        connectionPanel.setOpaque(false);

        // Círculo brillante personalizado con Graphics2D
        greenDot = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(currentStatusColor);
                // Dibujar círculo centrado
                int size = Math.min(getWidth(), getHeight());
                g2d.fillOval(0, (getHeight() - size) / 2, size, size);
                g2d.dispose();
            }
        };
        greenDot.setPreferredSize(new Dimension(8, 8));

        lblConnection = new JLabel("CONECTADO: crmgym_fase2");
        lblConnection.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblConnection.setForeground(currentStatusColor);

        connectionPanel.add(greenDot);
        connectionPanel.add(lblConnection);

        add(connectionPanel, "cell 0 0, alignx left");

        // --- Lado Derecho: Versión del sistema ---
        JLabel lblVersion = new JLabel("VERSIÓN 2.0.0 | Jun 24, 2026");
        lblVersion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVersion.setForeground(TEXT_COLOR);

        // Se alinea a la derecha ocupando el resto del espacio disponible (pushx)
        add(lblVersion, "cell 1 0, pushx, al right");
    }

    /**
     * Updates the connection status indicator and text dynamically.
     * @param connected true if database is online, false otherwise
     * @param databaseName the name of the active database file/schema
     */
    public void setConnectionStatus(boolean connected, String databaseName) {
        if (connected) {
            currentStatusColor = Color.decode("#10b981"); // Emerald Green
            lblConnection.setText("CONECTADO: " + databaseName);
            lblConnection.setForeground(currentStatusColor);
        } else {
            currentStatusColor = Color.decode("#ef4444"); // Alert Red
            lblConnection.setText("DESCONECTADO: Sin base de datos");
            lblConnection.setForeground(currentStatusColor);
        }
        greenDot.repaint(); // Force the Graphics2D to redraw the circle instantly
    }
}
