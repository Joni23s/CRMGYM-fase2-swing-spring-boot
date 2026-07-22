package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * =================================================================================
 * DISPOSITIVO DE RENDIMIENTO: EL PATRÓN FLYWEIGHT (PESO LIGERO) EN RENDERIZACIÓN SWING
 * =================================================================================
 */
public class DashboardTable extends JTable {

    private final TableCellRenderer defaultCellRenderer  = new DashboardCellRenderer();
    private final TableCellRenderer statusBadgeRenderer  = new StatusBadgeRenderer(true);
    private final TableCellEditor statusBadgeEditor;
    private final TableCellRenderer clienteCellRenderer  = new ClienteCellRenderer();

    public DashboardTable(DefaultTableModel model) {
        super(model);
        setRowHeight(46);
        setShowGrid(false);
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBackground(Theme.CARD_BG);
        setForeground(Theme.TEXT_ACTIVE);
        setSelectionBackground(Theme.BORDER_SLATE);
        setSelectionForeground(Theme.TEXT_ACTIVE);

        // Crear un menu basico para el editor
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem item1 = new JMenuItem("Cambiar Estado");
        popupMenu.add(item1);
        statusBadgeEditor = new StatusBadgeCellEditor(popupMenu);

        customizeHeader();
    }

    private void customizeHeader() {
        JTableHeader header = getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(Theme.FONT_CARD_TITLE);
                setForeground(Theme.TEXT_INACTIVE);
                setBackground(Theme.CARD_BG);
                setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_SLATE),
                        javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14)));
                String colName = table.getColumnName(column).toUpperCase();
                if (colName.contains("ESTADO")) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else if (colName.contains("MONTO") || colName.contains("PRECIO")) {
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                return this;
            }
        });
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 0) return clienteCellRenderer;
        Object value = getValueAt(row, column);
        if (value != null) {
            String strVal = value.toString().toUpperCase();
            if ("CONFIRMADO".equals(strVal) || "PENDIENTE".equals(strVal)
                    || "ACTIVO".equals(strVal) || "INACTIVO".equals(strVal)
                    || "TRUE".equals(strVal) || "FALSE".equals(strVal)) {
                String colName = getColumnName(column).toUpperCase();
                if (colName.contains("ESTADO")) {
                    return statusBadgeRenderer;
                }
            }
        } else if (column == 0) {
            return clienteCellRenderer;
        }
        return defaultCellRenderer;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        String colName = getColumnName(column).toUpperCase();
        if (colName.contains("ESTADO")) {
            return statusBadgeEditor;
        }
        return super.getCellEditor(row, column);
    }

    // =========================================================================
    // RENDERIZADOR DE CELDA ESTÁNDAR
    // =========================================================================
    private static class DashboardCellRenderer extends DefaultTableCellRenderer {
        public DashboardCellRenderer() { setOpaque(true); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            super.paintComponent(g);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(Theme.FONT_BODY);
            setForeground(Theme.TEXT_ACTIVE);
            setBackground(row % 2 == 0 ? Theme.CARD_BG : Theme.CARD_BG_ALT);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14));

            String text = value != null ? value.toString() : "";
            if (text.contains("$") || text.contains(",00")) {
                setFont(Theme.FONT_MONO);
                setHorizontalAlignment(JLabel.RIGHT);
            } else if (column == 2 && text.contains("/")) {
                setHorizontalAlignment(JLabel.CENTER);
            } else {
                setHorizontalAlignment(JLabel.LEFT);
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            return this;
        }
    }

    // =========================================================================
    // RENDERIZADOR FLYWEIGHT PARA NOMBRE DE CLIENTE + AVATAR DE INICIALES
    // Usa paintComponent() propio — nunca MigLayout inválido dentro de []
    // =========================================================================
    private static class ClienteCellRenderer extends JPanel implements TableCellRenderer {

        private String name     = "";
        private String initials = "";
        private Color  badgeBg  = Color.GRAY;
        private Color  cellBg   = Theme.CARD_BG;

        // Dimensiones del badge circular
        private static final int BADGE_SIZE  = 32;
        private static final int BADGE_LEFT  = 14;  // margen izquierdo
        private static final int TEXT_LEFT   = BADGE_LEFT + BADGE_SIZE + 10; // texto tras el badge

        public ClienteCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            name = value != null ? value.toString() : "";

            // Calcular iniciales
            String init = "";
            if (!name.isEmpty()) {
                String[] parts = name.trim().split("\\s+");
                if (parts.length >= 2) {
                    init = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
                } else if (parts[0].length() >= 2) {
                    init = parts[0].substring(0, 2).toUpperCase();
                } else {
                    init = parts[0].toUpperCase();
                }
            }
            initials = init;

            // Color semántico según las iniciales (Paleta Vintage: Tinta, Oxblood, Azul Apagado, Mostaza)
            switch (initials) {
                case "MG": badgeBg = Color.decode("#211B15"); break;
                case "JA": badgeBg = Color.decode("#8C2320"); break;
                case "MO": badgeBg = Color.decode("#2E4057"); break;
                case "CL": badgeBg = Color.decode("#C68A1E"); break;
                default:   badgeBg = Color.decode("#5C4E43"); break;
            }

            cellBg = isSelected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Theme.CARD_BG : Theme.CARD_BG_ALT);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            // Fondo de celda
            g2.setColor(cellBg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Calcular posición vertical centrada
            int cy = (getHeight() - BADGE_SIZE) / 2;

            // Círculo del badge
            g2.setColor(badgeBg);
            g2.fillOval(BADGE_LEFT, cy, BADGE_SIZE, BADGE_SIZE);

            // Iniciales centradas en el círculo (Oswald)
            g2.setFont(new Font("Oswald", Font.BOLD, 11));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = BADGE_LEFT + (BADGE_SIZE - fm.stringWidth(initials)) / 2;
            int ty = cy + (BADGE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initials, tx, ty);

            // Nombre del cliente (Courier Prime)
            g2.setFont(new Font("Courier Prime", Font.BOLD, 13));
            g2.setColor(Theme.TEXT_ACTIVE);
            FontMetrics fmName = g2.getFontMetrics();
            int nameY = (getHeight() - fmName.getHeight()) / 2 + fmName.getAscent();
            g2.drawString(name, TEXT_LEFT, nameY);

            g2.dispose();
        }
    }

}
