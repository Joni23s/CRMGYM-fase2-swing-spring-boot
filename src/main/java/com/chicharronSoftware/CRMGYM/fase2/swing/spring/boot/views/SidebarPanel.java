package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views;

import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * [MEJORA EDITORIAL VINTAGE] SidebarPanel adaptado al sistema de diseño Tinta & Papel Envejecido.
 * - Fondo de Tinta Oscura (#211B15).
 * - Botones activos en Rojo Sangre de Toro (#8C2320) con indicador lineal Mostaza (#C68A1E) y 0 arc.
 */
@Component
public class SidebarPanel extends JPanel {

    private final List<JButton> menuButtons = new ArrayList<>();
    private JButton activeButton;

    // Colores Vintage Editorial
    private static final Color BG_COLOR = Color.decode("#211B15"); // Tinta oscura
    private static final Color TEXT_ACTIVE = Color.decode("#EFE3C8"); // Papel envejecido
    private static final Color TEXT_INACTIVE = Color.decode("#A89885"); // Tinta atenuada
    private static final Color BTN_ACTIVE_BG = Color.decode("#8C2320"); // Rojo sangre de toro
    private static final Color ACCENT_MUSTARD = Color.decode("#C68A1E"); // Mostaza
    private static final Color SEPARATOR_COLOR = Color.decode("#3B3127"); // Separador

    public SidebarPanel() {
        initComponents();
    }

    private void initComponents() {
        setBackground(BG_COLOR);
        setLayout(new MigLayout("wrap 1, ins 18 14 18 14, gapy 10, fillx"));

        // --- 1. Logotipo / Header ---
        JPanel brandPanel = new JPanel(new MigLayout("ins 0, gapx 10, aligny center"));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel(new VectorIcon("logo", 26));
        JLabel lblBrandName = new JLabel("CRMGYM");
        lblBrandName.setFont(new Font("Bevan", Font.BOLD, 20));
        lblBrandName.setForeground(TEXT_ACTIVE);

        brandPanel.add(lblLogo);
        brandPanel.add(lblBrandName);
        add(brandPanel, "gapbottom 6, alignx left");

        // --- 2. Separador de Header ---
        JSeparator headerSeparator = new JSeparator(JSeparator.HORIZONTAL);
        headerSeparator.setForeground(SEPARATOR_COLOR);
        headerSeparator.setBackground(SEPARATOR_COLOR);
        add(headerSeparator, "growx, gapbottom 10");

        // --- 3. Botones de Navegación ---
        JButton btnInicio = createMenuButton("Inicio", new VectorIcon("home", 18));
        JButton btnSocios = createMenuButton("Socios", new VectorIcon("users", 18));
        JButton btnPlanes = createMenuButton("Planes", new VectorIcon("file-text", 18));
        JButton btnPagos = createMenuButton("Pagos", new VectorIcon("credit-card", 18));
        JButton btnHistorial = createMenuButton("Historial", new VectorIcon("history", 18));

        add(btnInicio, "growx");
        add(btnSocios, "growx");
        add(btnPlanes, "growx");
        add(btnPagos, "growx");
        add(btnHistorial, "growx");

        // --- 4. Widget de Perfil de Usuario ---
        JPanel userProfilePanel = new JPanel(new MigLayout("ins 8 4 8 4, gapx 10, fillx", "[][grow]", "[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                super.paintComponent(g);
            }
        };
        userProfilePanel.setOpaque(false);

        // Avatar circular solido en Azul Apagado (#2E4057) sin gradientes
        JPanel initialsBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                g2d.setColor(Color.decode("#2E4057"));
                g2d.fillOval(0, 0, getWidth(), getHeight());

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Oswald", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "JA";
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, tx, ty);
                g2d.dispose();
            }
        };
        initialsBadge.setPreferredSize(new Dimension(34, 34));
        initialsBadge.setOpaque(false);

        JPanel textStack = new JPanel(new MigLayout("wrap 1, ins 0, gapy 2"));
        textStack.setOpaque(false);

        JPanel namePanel = new JPanel(new MigLayout("ins 0, gapx 6, fillx", "[grow][]", "[]"));
        namePanel.setOpaque(false);

        JLabel lblUsername = new JLabel("Jonathan A.");
        lblUsername.setFont(new Font("Oswald", Font.BOLD, 12));
        lblUsername.setForeground(TEXT_ACTIVE);

        namePanel.add(lblUsername, "cell 0 0, alignx left, aligny center");

        JLabel lblRole = new JLabel("Administrador");
        lblRole.setFont(new Font("Courier Prime", Font.PLAIN, 11));
        lblRole.setForeground(TEXT_INACTIVE);

        JLabel lblStatus = new JLabel("<html><font color='#8C5E12'>●</font> <font color='#A89885'>En línea</font></html>");
        lblStatus.setFont(new Font("Courier Prime", Font.BOLD, 10));

        textStack.add(namePanel, "growx");
        textStack.add(lblRole);
        textStack.add(lblStatus);

        userProfilePanel.add(initialsBadge, "cell 0 0, aligny center");
        userProfilePanel.add(textStack, "cell 1 0, aligny center");

        add(userProfilePanel, "growx, pushy, bottom, gaptop push");

        setActiveButton(btnInicio);
    }

    private JButton createMenuButton(String text, Icon icon) {
        JButton button = new JButton(text.toUpperCase(), icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                if (this == activeButton) {
                    // Fondo solido Oxblood (#8C2320) con 0 arc
                    g2d.setColor(BTN_ACTIVE_BG);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Indicador lineal izquierdo Mostaza (#C68A1E) de 4px
                    g2d.setColor(ACCENT_MUSTARD);
                    g2d.fillRect(0, 0, 4, getHeight());
                } else if (this.getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 12));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setIconTextGap(14);
        button.setFont(new Font("Oswald", Font.BOLD, 13));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 16));
        button.putClientProperty("JButton.buttonType", "toolBarButton");
        button.putClientProperty("FlatLaf.style", "arc: 0;");
        button.setForeground(TEXT_INACTIVE);

        button.addActionListener(e -> setActiveButton(button));

        menuButtons.add(button);
        return button;
    }

    public void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setForeground(TEXT_INACTIVE);
        }
        activeButton = button;
        activeButton.setForeground(TEXT_ACTIVE);
        repaint();
    }

    public void setActiveButtonByText(String text) {
        for (JButton btn : menuButtons) {
            if (btn.getText().equalsIgnoreCase(text)) {
                setActiveButton(btn);
                break;
            }
        }
    }

    public void addNavigationListener(String buttonText, ActionListener listener) {
        for (JButton btn : menuButtons) {
            if (btn.getText().equalsIgnoreCase(buttonText)) {
                btn.addActionListener(listener);
                break;
            }
        }
    }
}
