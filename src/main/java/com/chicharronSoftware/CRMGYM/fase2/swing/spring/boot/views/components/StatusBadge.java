package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * [MEJORA EDITORIAL VINTAGE] StatusBadge con diseño de Sello de Tinta (Stamp).
 * Presenta un borde doble recto (arc = 0) con rotación ligera (-3° a +3°) y tipografía
 * estilo sello estampado de imprenta.
 */
public class StatusBadge extends JLabel {

    public enum BadgeType {
        SUCCESS,
        WARNING,
        ERROR,
        NEUTRAL
    }

    private BadgeType currentType;
    private Color stampColor;
    private double rotationAngle = 0.0;

    public StatusBadge(String text, BadgeType type) {
        super(text);
        setFont(new Font("Oswald", Font.BOLD, 11));
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
        updateType(text, type);
    }

    public void updateType(String text, BadgeType type) {
        setText(text != null ? text.toUpperCase() : "");
        this.currentType = type;

        switch (type) {
            case SUCCESS:
                this.stampColor = Color.decode("#211B15"); // Tinta oscura
                this.rotationAngle = Math.toRadians(3.0);
                break;
                
            case WARNING:
                this.stampColor = Color.decode("#8C5E12"); // Mostaza legible
                this.rotationAngle = Math.toRadians(-3.0);
                break;
                
            case ERROR:
                this.stampColor = Color.decode("#8C2320"); // Sangre de Toro
                this.rotationAngle = Math.toRadians(-2.5);
                break;
                
            case NEUTRAL:
            default:
                this.stampColor = Theme.TEXT_INACTIVE;
                this.rotationAngle = 0.0;
                break;
        }

        setForeground(stampColor);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int textW = fm.stringWidth(getText() != null ? getText() : "");
        return new Dimension(textW + 24, 26);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            Theme.enableHighFidelity(g2d);

            int w = getWidth();
            int h = getHeight();

            FontMetrics fm = g2d.getFontMetrics(getFont());
            int textW = fm.stringWidth(getText() != null ? getText() : "");
            int badgeW = textW + 18;
            int badgeH = 20;

            int cx = w / 2;
            int cy = h / 2;

            // Rotar el contexto gráfico para dar el efecto de sello estampado manual
            g2d.rotate(rotationAngle, cx, cy);

            int x = cx - badgeW / 2;
            int y = cy - badgeH / 2;

            // Fondo suave translúcido
            g2d.setColor(new Color(stampColor.getRed(), stampColor.getGreen(), stampColor.getBlue(), 15));
            g2d.fillRect(x, y, badgeW, badgeH);

            // Borde Doble Recto de Sello (Arc = 0)
            g2d.setColor(stampColor);
            
            // Borde exterior grueso
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRect(x, y, badgeW - 1, badgeH - 1);

            // Borde interior concéntrico fino (Doble Borde)
            g2d.setStroke(new BasicStroke(0.8f));
            g2d.drawRect(x + 2, y + 2, badgeW - 5, badgeH - 5);

            // Dibujar el Texto Estampado
            g2d.setFont(getFont());
            g2d.setColor(stampColor);
            int textX = cx - textW / 2;
            int textY = cy - fm.getHeight() / 2 + fm.getAscent();
            g2d.drawString(getText(), textX, textY);

        } finally {
            g2d.dispose();
        }
    }
}
