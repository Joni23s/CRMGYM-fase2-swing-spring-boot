package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views;

import java.awt.*;
import javax.swing.Icon;

public class VectorIcon implements Icon {
    private final String type;
    private final int size;
    private final Color defaultColor;

    public VectorIcon(String type, int size, Color defaultColor) {
        this.type = type;
        this.size = size;
        this.defaultColor = defaultColor;
    }

    public VectorIcon(String type, int size) {
        this(type, size, null);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        Color color = defaultColor;
        if (color == null && c != null) {
            color = c.getForeground();
        }
        if (color == null) {
            color = Color.WHITE;
        }
        g2.setColor(color);

        int w = size;
        int h = size;

        switch (type.toLowerCase()) {
            case "logo":
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw circle background with blue gradient
                g2.setPaint(new GradientPaint(x, y, Color.decode("#3b82f6"), x + w, y + h, Color.decode("#1d4ed8")));
                g2.fillOval(x, y, w, h);
                // Draw dumbbells in white
                g2.setColor(Color.WHITE);
                int cx = x + w / 2;
                int cy = y + h / 2;
                // Bar
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 7, cy, cx + 7, cy);
                // Inner weights
                g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 6, cy - 4, cx - 6, cy + 4);
                g2.drawLine(cx + 6, cy - 4, cx + 6, cy + 4);
                // Outer plates
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 9, cy - 2, cx - 9, cy + 2);
                g2.drawLine(cx + 9, cy - 2, cx + 9, cy + 2);
                break;

            case "home":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Roof
                g2.drawLine(x + 2, y + h / 2, x + w / 2, y + 2);
                g2.drawLine(x + w / 2, y + 2, x + w - 2, y + h / 2);
                // Body
                g2.drawRect(x + 4, y + h / 2, w - 8, h / 2 - 2);
                // Door
                g2.drawLine(x + w / 2 - 2, y + h - 2, x + w / 2 - 2, y + h / 2 + 4);
                g2.drawLine(x + w / 2 + 2, y + h - 2, x + w / 2 + 2, y + h / 2 + 4);
                g2.drawLine(x + w / 2 - 2, y + h / 2 + 4, x + w / 2 + 2, y + h / 2 + 4);
                break;

            case "users":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // User 1 (front)
                g2.drawOval(x + w / 2 - 4, y + 2, 8, 8); // head
                g2.drawArc(x + 2, y + 11, w - 4, 10, 0, 180); // shoulder
                
                // User 2 (behind left)
                g2.drawOval(x + 2, y + 5, 5, 5);
                g2.drawArc(x + 1, y + 11, 8, 6, 45, 135);
                break;

            case "file-text":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 3, 3);
                g2.drawLine(x + 6, y + 6, x + w - 6, y + 6);
                g2.drawLine(x + 6, y + 10, x + w - 6, y + 10);
                g2.drawLine(x + 6, y + 14, x + w - 9, y + 14);
                break;

            case "file-check":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 3, 3);
                // Draw checkmark inside instead of text lines
                g2.drawLine(x + w / 2 - 3, y + h / 2 + 1, x + w / 2 - 1, y + h / 2 + 3);
                g2.drawLine(x + w / 2 - 1, y + h / 2 + 3, x + w / 2 + 3, y + h / 2 - 2);
                break;

            case "credit-card":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(x + 2, y + 3, w - 4, h - 6, 3, 3);
                g2.fillRect(x + 2, y + 7, w - 4, 3); // Magnetic stripe
                break;

            case "credit-card-plus":
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(x + 1, y + 3, w - 5, h - 8, 2, 2);
                g2.fillRect(x + 1, y + 6, w - 5, 2);
                // Plus sign
                g2.drawLine(x + w - 4, y + h - 7, x + w - 4, y + h - 1);
                g2.drawLine(x + w - 7, y + h - 4, x + w - 1, y + h - 4);
                break;

            case "history":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x + 2, y + 2, w - 4, h - 4, 45, 270);
                // Arrow tip
                g2.drawLine(x + w / 2, y + 2, x + w / 2 - 3, y + 5);
                g2.drawLine(x + w / 2, y + 2, x + w / 2 + 3, y + 5);
                // Clock hands
                g2.drawLine(x + w / 2, y + h / 2, x + w / 2, y + 6);
                g2.drawLine(x + w / 2, y + h / 2, x + w / 2 + 4, y + h / 2 + 2);
                break;

            case "sun":
                g2.setColor(Color.decode("#f59e0b")); // bright orange-yellow
                g2.fillOval(x + 5, y + 5, w - 10, h - 10);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Rays
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    int x1 = (int) (x + w / 2 + (w / 2 - 4) * Math.cos(angle));
                    int y1 = (int) (y + h / 2 + (h / 2 - 4) * Math.sin(angle));
                    int x2 = (int) (x + w / 2 + (w / 2 - 1) * Math.cos(angle));
                    int y2 = (int) (y + h / 2 + (h / 2 - 1) * Math.sin(angle));
                    g2.drawLine(x1, y1, x2, y2);
                }
                break;

            case "bell":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x + 4, y + 3, w - 8, h - 6, 0, 180);
                g2.drawLine(x + 2, y + h - 5, x + w - 2, y + h - 5);
                g2.drawArc(x + w / 2 - 2, y + h - 5, 4, 4, 180, 180);
                break;

            case "avatar":
                // Draw rounded profile avatar
                g2.setPaint(new GradientPaint(x, y, Color.decode("#3b82f6"), x + w, y + h, Color.decode("#1d4ed8")));
                g2.fillOval(x, y, w, h);
                // Draw user silhouette
                g2.setColor(Color.WHITE);
                g2.fillOval(x + w / 4, y + h / 5, w / 2, h / 2 - 1);
                g2.fillArc(x + w / 8, y + h * 3 / 5, w * 3 / 4, h * 2 / 3, 0, 180);
                break;

            case "recaudacion":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Money bag outline
                g2.drawRoundRect(x + 2, y + 5, w - 8, h - 8, 3, 3);
                // Ties
                g2.drawLine(x + 5, y + 5, x + w - 11, y + 5);
                // Overlapping card outline
                g2.setColor(Color.decode("#06b6d4"));
                g2.drawRoundRect(x + 8, y + 8, w - 10, h - 11, 2, 2);
                break;

            case "dollar":
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Draw S curves
                g2.drawArc(x + w / 2 - 3, y + 4, 6, 6, 90, 180); // top curve
                g2.drawArc(x + w / 2 - 3, y + 9, 6, 6, 270, 180); // bottom curve
                // Vertical line
                g2.drawLine(x + w / 2, y + 2, x + w / 2, y + h - 2);
                break;

            case "check":
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 4, y + h / 2 + 1, x + w / 2 - 1, y + h / 2 + 3);
                g2.drawLine(x + w / 2 - 1, y + h / 2 + 3, x + w - 4, y + 4);
                break;

            case "user-plus":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // User
                g2.drawOval(x + 3, y + 2, 6, 6);
                g2.drawArc(x + 1, y + 9, 10, 6, 0, 180);
                // Plus
                g2.drawLine(x + w - 4, y + 4, x + w - 4, y + 10);
                g2.drawLine(x + w - 7, y + 7, x + w - 1, y + 7);
                break;

            case "clipboard":
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(x + 3, y + 4, w - 6, h - 6, 2, 2);
                // Clipboard clip
                g2.fillRect(x + w / 2 - 3, y + 1, 6, 3);
                // Lines
                g2.drawLine(x + 6, y + 8, x + w - 6, y + 8);
                g2.drawLine(x + 6, y + 12, x + w - 6, y + 12);
                break;
        }

        g2.dispose();
    }
}
