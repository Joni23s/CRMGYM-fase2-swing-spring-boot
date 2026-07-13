package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views;

import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@Component
public class SidebarPanel extends JPanel {

    private final List<JButton> menuButtons = new ArrayList<>();
    private JButton activeButton;

    // Colores premium Slate
    private static final Color BG_COLOR = Color.decode("#090d16"); // Slate azul ultra oscuro
    private static final Color TEXT_ACTIVE = Color.decode("#f8fafc"); // Slate 50
    private static final Color TEXT_INACTIVE = Color.decode("#94a3b8"); // Slate 400
    private static final Color BTN_ACTIVE_BG = Color.decode("#1e293b"); // Slate 800
    private static final Color ACCENT_BLUE = Color.decode("#3b82f6"); // Blue 500
    private static final Color SEPARATOR_COLOR = Color.decode("#1e293b"); // Slate 800 para el separador

    public SidebarPanel() {
        initComponents();
    }

    private void initComponents() {
        setBackground(BG_COLOR);
        // MigLayout vertical
        setLayout(new MigLayout("wrap 1, ins 20, gapy 12, fillx"));

        // --- 1. Logotipo / Header ---
        JPanel brandPanel = new JPanel(new MigLayout("ins 0, gapx 10, aligny center"));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel(new VectorIcon("logo", 28));
        JLabel lblBrandName = new JLabel("CRMGYM");
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrandName.setForeground(TEXT_ACTIVE);

        brandPanel.add(lblLogo);
        brandPanel.add(lblBrandName);
        add(brandPanel, "gapbottom 8, alignx left");

        // --- 2. Separador de Header ---
        JSeparator headerSeparator = new JSeparator(JSeparator.HORIZONTAL);
        headerSeparator.setForeground(SEPARATOR_COLOR);
        headerSeparator.setBackground(SEPARATOR_COLOR);
        add(headerSeparator, "growx, gapbottom 12");

        // --- 3. Botones de Navegación agrupados arriba ---
        JButton btnInicio = createMenuButton("Inicio", new VectorIcon("home", 20));
        JButton btnSocios = createMenuButton("Socios", new VectorIcon("users", 20));
        JButton btnPlanes = createMenuButton("Planes", new VectorIcon("file-text", 20));
        JButton btnPagos = createMenuButton("Pagos", new VectorIcon("credit-card", 20));
        JButton btnHistorial = createMenuButton("Historial", new VectorIcon("history", 20));

        add(btnInicio, "growx");
        add(btnSocios, "growx");
        add(btnPlanes, "growx");
        add(btnPagos, "growx");
        add(btnHistorial, "growx");

        // --- 4. Widget de Perfil de Usuario (Abajo del todo) ---
        JPanel userProfilePanel = new JPanel(new MigLayout("ins 10 4 10 4, gapx 12, fillx", "[][grow]", "[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                // Forzar anti-aliasing de texto de alta fidelidad para los labels del perfil
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                super.paintComponent(g);
            }
        };
        userProfilePanel.setOpaque(false);

        // Badge de Iniciales Circular con fondo gradiente azul
        JPanel initialsBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Fondo circular con gradiente
                g2d.setPaint(new GradientPaint(0, 0, Color.decode("#3b82f6"), getWidth(), getHeight(),
                        Color.decode("#1d4ed8")));
                g2d.fillOval(0, 0, getWidth(), getHeight());

                // Dibujar texto "JA" en blanco
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "JA";
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, tx, ty);
                g2d.dispose();
            }
        };
        initialsBadge.setPreferredSize(new Dimension(36, 36));
        initialsBadge.setOpaque(false);

        // Contenedor de Texto (Nombre, Rol y Estado)
        JPanel textStack = new JPanel(new MigLayout("wrap 1, ins 0, gapy 2"));
        textStack.setOpaque(false);

        // Subpanel para Nombre y Chevron
        JPanel namePanel = new JPanel(new MigLayout("ins 0, gapx 6, fillx", "[grow][]", "[]"));
        namePanel.setOpaque(false);

        JLabel lblUsername = new JLabel("Jonathan A.");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(TEXT_ACTIVE);

        JComponent lblChevron = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TEXT_INACTIVE);
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Dibujar doble chevron apuntando hacia abajo
                g2d.drawLine(1, 3, 4, 6);
                g2d.drawLine(4, 6, 7, 3);
                g2d.drawLine(1, 7, 4, 10);
                g2d.drawLine(4, 10, 7, 7);
                g2d.dispose();
            }
        };
        lblChevron.setPreferredSize(new Dimension(9, 13));

        namePanel.add(lblUsername, "cell 0 0, alignx left, aligny center");
        namePanel.add(lblChevron, "cell 1 0, alignx right, aligny center, w 9!, h 13!");

        JLabel lblRole = new JLabel("Administrador");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(TEXT_INACTIVE);

        // El punto "●" es verde, pero el texto "En línea" es gris/inactivo
        JLabel lblStatus = new JLabel(
                "<html><font color='#10b981'>●</font> <font color='#94a3b8'>En línea</font></html>");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 10));

        textStack.add(namePanel, "growx");
        textStack.add(lblRole);
        textStack.add(lblStatus);

        userProfilePanel.add(initialsBadge, "cell 0 0, aligny center");
        userProfilePanel.add(textStack, "cell 1 0, aligny center");

        // Agregamos el widget de perfil al final empujando verticalmente
        add(userProfilePanel, "growx, pushy, bottom, gaptop push");

        // Inicializamos "Inicio" como activo por defecto
        setActiveButton(btnInicio);
    }

    private JButton createMenuButton(String text, Icon icon) {
        JButton button = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                if (this == activeButton) {
                    // Fondo redondeado activo con gradiente de brillo azul en la izquierda
                    GradientPaint activeBgGradient = new GradientPaint(
                            0, 0, new Color(59, 130, 246, 45), // ~18% opacidad azul en el extremo izquierdo
                            getWidth() * 0.4f, 0, BTN_ACTIVE_BG // Slate 800 en el centro y derecha
                    );
                    g2d.setPaint(activeBgGradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                    // Línea vertical izquierda de acento azul (abarca todo el alto y redondeado)
                    g2d.setColor(ACCENT_BLUE);
                    g2d.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                } else if (this.getModel().isRollover()) {
                    // Efecto hover sutil semi-transparente para botones inactivos
                    g2d.setColor(new Color(255, 255, 255, 10)); // ~4% opacidad blanca sobre fondo oscuro
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setIconTextGap(16);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Ajustamos la separación interna usando un EmptyBorder (26px a la izquierda
        // para alejar el icono del indicador azul)
        button.setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 20));

        // Estilos específicos de FlatLaf
        button.putClientProperty("JButton.buttonType", "toolBarButton");
        button.putClientProperty("FlatLaf.style", "arc: 12;");
        button.setForeground(TEXT_INACTIVE);

        // Eventos básicos para mantener la consistencia
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

    /**
     * Permite registrar acciones externas para la navegación del menú lateral.
     */
    public void addNavigationListener(String buttonText, ActionListener listener) {
        for (JButton btn : menuButtons) {
            if (btn.getText().equalsIgnoreCase(buttonText)) {
                btn.addActionListener(listener);
                break;
            }
        }
    }
}
