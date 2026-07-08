package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * =================================================================================
 * METRIC CARD SYSTEM: COMPONENTE REUTILIZABLE DE KPI EN FORMATO HORIZONTAL PROMINENTE
 * =================================================================================
 * Layout: [Icono 48px] [Textos grow] [Sparkline 72px]
 * Altura mínima garantizada: 80px para evitar colapso en layouts compactos.
 */
public class MetricCard extends JPanel {

    private final String  title;
    private String        value; // [MEJORA JUNIOR] Le quitamos el "final" para poder modificar su valor dinámicamente.
    private final String  subtitle;
    private final String  iconType;
    private final Color   subtitleColor;
    private final boolean showTrend;
    private final int[]   trendData;

    // [MEJORA JUNIOR] Guardamos la etiqueta de valor como variable de clase para actualizarla con un método set.
    private JLabel valueLabel;

    // Dimensiones del sparkline / barchart (relativas al área derecha de la tarjeta)
    private static final int SPARK_W = 72;
    private static final int SPARK_H = 28;
    private static final int SPARK_MARGIN_RIGHT = 22;

    public MetricCard(String title, String value, String subtitle, String iconType,
                      Color subtitleColor, boolean showTrend, int[] trendData) {
        this.title         = title;
        this.value         = value;
        this.subtitle      = subtitle;
        this.iconType      = iconType;
        this.subtitleColor = subtitleColor;
        this.trendData     = trendData;
        this.showTrend     = (trendData != null && trendData.length > 0) && showTrend;

        setOpaque(false);
        // Altura mínima para evitar colapso
        setMinimumSize(new Dimension(180, 80));

        // Layout: [Icono 48px fijo] | [Textos grow] | [Área sparkline 80px fija]
        setLayout(new MigLayout(
                "ins 18 22 18 22, fill, aligny center, gapx 14",
                "[48px!][grow][" + (SPARK_W + SPARK_MARGIN_RIGHT) + "px!]",
                "[grow]"));

        // ── 1. Caja del Icono (48×48, fondo semitransparente) ─────────────────
        JPanel iconBox = new JPanel(new MigLayout("ins 0, align center, fill")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(resolveIconBgColor());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2d.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(48, 48));
        iconBox.setOpaque(false);

        JLabel iconLabel = new JLabel(new VectorIcon(iconType, 22, resolveIconFgColor()));
        iconBox.add(iconLabel, "align center");
        add(iconBox, "cell 0 0, aligny center");

        // ── 2. Stack de Textos (Título, Valor, Subtítulo) ────────────────────
        JPanel textPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 2"));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(this.title);
        titleLabel.setFont(Theme.FONT_CARD_TITLE);
        titleLabel.setForeground(Theme.TEXT_INACTIVE);

        this.valueLabel = new JLabel(this.value); // [MEJORA JUNIOR] Inicializamos la variable de clase.
        // Fuente ligeramente más pequeña para "Recaudación" (tiene texto largo)
        if (this.title != null && this.title.contains("Recaudación")) {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        } else {
            valueLabel.setFont(Theme.FONT_DASHBOARD_TITLE);
        }
        valueLabel.setForeground(Theme.TEXT_ACTIVE);

        JLabel subtitleLabel = new JLabel(this.subtitle);
        subtitleLabel.setFont(Theme.FONT_FOOTER);
        subtitleLabel.setForeground(this.subtitleColor);

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        textPanel.add(subtitleLabel);
        add(textPanel, "cell 1 0, aligny center, growx");

        // ── 3. Placeholder invisible para que MigLayout reserve espacio ───────
        // El sparkline real se pinta en paintComponent() encima de esta región
        JPanel trendPlaceholder = new JPanel();
        trendPlaceholder.setOpaque(false);
        trendPlaceholder.setPreferredSize(new Dimension(SPARK_W, SPARK_H));
        add(trendPlaceholder, "cell 2 0, aligny center");
    }

    // ── Color helpers ─────────────────────────────────────────────────────────
    private Color resolveIconBgColor() {
        if ("users".equals(iconType))                          return new Color( 59, 130, 246, 30);
        if ("file-text".equals(iconType) || "file-check".equals(iconType)) return new Color( 16, 185, 129, 30);
        return new Color(139, 92, 246, 30); // dollar / default → púrpura
    }

    private Color resolveIconFgColor() {
        if ("users".equals(iconType))                          return Color.decode("#60a5fa");
        if ("file-text".equals(iconType) || "file-check".equals(iconType)) return Color.decode("#34d399");
        return Color.decode("#c084fc");
    }

    // ── Pintado de la tarjeta + sparkline ─────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            Theme.enableHighFidelity(g2d);

            int w = getWidth();
            int h = getHeight();

            // Fondo de la tarjeta
            g2d.setColor(Theme.CARD_BG);
            g2d.fillRoundRect(0, 0, w, h, Theme.ARC_CARD, Theme.ARC_CARD);

            // Borde Slate sutil
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(0, 0, w - 1, h - 1, Theme.ARC_CARD, Theme.ARC_CARD);

            // Sparkline / barchart (pintado sobre el placeholder invisible)
            if (showTrend && trendData != null && trendData.length >= 2) {
                if ("dollar".equals(iconType) || "recaudacion".equals(iconType)) {
                    drawMicroBarChart(g2d, w, h);
                } else {
                    drawMicroSparkline(g2d, w, h);
                }
            }

        } finally {
            g2d.dispose();
        }
    }

    private void drawMicroSparkline(Graphics2D g2d, int cardWidth, int cardHeight) {
        int xStart = cardWidth - SPARK_W - SPARK_MARGIN_RIGHT;
        int yStart = (cardHeight - SPARK_H) / 2;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int v : trendData) { if (v < min) min = v; if (v > max) max = v; }
        int range = (max - min == 0) ? 1 : max - min;

        int n = trendData.length;
        int[] px = new int[n];
        int[] py = new int[n];
        for (int i = 0; i < n; i++) {
            px[i] = xStart + (i * SPARK_W) / (n - 1);
            py[i] = yStart + SPARK_H - ((trendData[i] - min) * SPARK_H) / range;
        }

        Color c0 = "users".equals(iconType) ? Color.decode("#2563eb") : Color.decode("#059669");
        Color c1 = "users".equals(iconType) ? Color.decode("#60a5fa") : Color.decode("#34d399");

        g2d.setPaint(new GradientPaint(xStart, yStart, c0, xStart + SPARK_W, yStart, c1));
        g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < n - 1; i++) {
            g2d.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
        }
    }

    private void drawMicroBarChart(Graphics2D g2d, int cardWidth, int cardHeight) {
        int barW   = 4;
        int gap    = 4;
        int n      = trendData.length;
        int totalW = n * barW + (n - 1) * gap;

        int xStart = cardWidth - totalW - SPARK_MARGIN_RIGHT;
        int yStart = (cardHeight - SPARK_H) / 2;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int v : trendData) { if (v < min) min = v; if (v > max) max = v; }
        int range = (max - min == 0) ? 1 : max - min;

        g2d.setColor(Color.decode("#8b5cf6"));
        for (int i = 0; i < n; i++) {
            int barH = ((trendData[i] - min) * SPARK_H) / range;
            if (barH < 4) barH = 4;
            int x = xStart + i * (barW + gap);
            int y = yStart + SPARK_H - barH;
            g2d.fillRoundRect(x, y, barW, barH, 2, 2);
        }
    }

    // [MEJORA JUNIOR] Método público para actualizar el valor numérico o de texto de la tarjeta
    // sin tener que recrear todo el componente gráfico.
    public void setValue(String value) {
        this.value = value;
        if (valueLabel != null) {
            valueLabel.setText(value);
        }
    }
}
