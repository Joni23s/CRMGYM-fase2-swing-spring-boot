package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderizador centralizado de insignias de estado para tablas.
 * Utiliza el componente StatusBadge para pintar el estado de la celda de manera unificada.
 */
public class StatusBadgeRenderer extends JPanel implements TableCellRenderer {

    private final StatusBadge badge;
    private final boolean showActions;
    private Color cellBg = Theme.CARD_BG;
    private Color cellFg = Theme.TEXT_ACTIVE;

    public StatusBadgeRenderer(boolean showActions) {
        this.showActions = showActions;
        this.badge = new StatusBadge("", StatusBadge.BadgeType.NEUTRAL);
        setLayout(new BorderLayout());
        setOpaque(true);
        add(badge, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String text = value != null ? value.toString() : "";
        StatusBadge.BadgeType type = determineType(text);
        badge.updateType(text, type);

        if (isSelected) {
            cellBg = table.getSelectionBackground();
            cellFg = table.getSelectionForeground();
        } else {
            cellBg = (row % 2 == 0) ? Theme.CARD_BG : Theme.CARD_BG_ALT;
            cellFg = Theme.TEXT_ACTIVE;
        }
        setBackground(cellBg);
        return this;
    }

    private StatusBadge.BadgeType determineType(String text) {
        String upper = text.toUpperCase();
        if (upper.equals("ACTIVO") || upper.equals("TRUE") || upper.equals("CONFIRMADO") || upper.equals("PAGADO")) {
            return StatusBadge.BadgeType.SUCCESS;
        } else if (upper.equals("PENDIENTE")) {
            return StatusBadge.BadgeType.WARNING;
        } else if (upper.equals("INACTIVO") || upper.equals("FALSE") || upper.equals("CANCELADO") || upper.equals("VENCIDO") || upper.equals("VENCIDA")) {
            return StatusBadge.BadgeType.ERROR;
        }
        return StatusBadge.BadgeType.NEUTRAL;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showActions) {
            Graphics2D g2 = (Graphics2D) g.create();
            Theme.enableHighFidelity(g2);
            g2.setColor(cellFg);
            int dotsW = 4;
            int gap = 3;
            int rightPadding = 12;
            int startX = getWidth() - rightPadding - dotsW;
            int startY = (getHeight() - (dotsW * 3 + gap * 2)) / 2;
            
            for (int i = 0; i < 3; i++) {
                g2.fillOval(startX, startY + (dotsW + gap) * i, dotsW, dotsW);
            }
            g2.dispose();
        }
    }
}
