package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * [MEJORA EDITORIAL VINTAGE] Componente de Métricas KPI estilo Tarjeta Plano Vintage (Arc = 0).
 */
public class MetricCard extends JPanel {

    private final String title;
    private String value;
    private final String subtitle;
    private JLabel valueLabel;

    public MetricCard(String title, String value, String subtitle, String iconType,
                      Color subtitleColor, boolean showTrend, int[] trendData) {
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;

        setOpaque(false);
        setMinimumSize(new Dimension(180, 85));

        setLayout(new MigLayout("ins 14 18 14 18, fill, aligny center", "[grow]", "[pref]4[pref]2[pref]"));

        // ── 1. Título de la tarjeta (Oswald Mayúsculas) ────────────────────────
        JLabel titleLabel = new JLabel(this.title != null ? this.title.toUpperCase() : "");
        titleLabel.setFont(new Font("Oswald", Font.BOLD, 12));
        titleLabel.setForeground(Theme.TEXT_INACTIVE);
        add(titleLabel, "wrap");

        // ── 2. Valor destacado de la métrica (Bevan / Courier Prime) ───────────
        this.valueLabel = new JLabel(this.value);
        if (this.title != null && this.title.contains("Recaudación")) {
            valueLabel.setFont(new Font("Bevan", Font.BOLD, 22));
            valueLabel.setForeground(Color.decode("#8C2320")); // Oxblood
        } else {
            valueLabel.setFont(new Font("Bevan", Font.BOLD, 24));
            valueLabel.setForeground(Theme.TEXT_ACTIVE);
        }
        add(valueLabel, "wrap");

        // ── 3. Subtítulo (Courier Prime / Oswald) ──────────────────────────────
        JLabel subtitleLabel = new JLabel(this.subtitle);
        subtitleLabel.setFont(new Font("Courier Prime", Font.PLAIN, 11));
        subtitleLabel.setForeground(Theme.TEXT_INACTIVE);
        add(subtitleLabel, "growx");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            Theme.enableHighFidelity(g2d);

            int w = getWidth();
            int h = getHeight();

            // Fondo plano de papel
            g2d.setColor(Theme.CARD_BG);
            g2d.fillRect(0, 0, w, h);

            // Borde recto 1px de Tinta
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRect(0, 0, w - 1, h - 1);

            // Adorno superior de línea mostaza `#C68A1E` de 2px
            g2d.setColor(Color.decode("#C68A1E"));
            g2d.fillRect(0, 0, w, 2);

        } finally {
            g2d.dispose();
        }
    }

    public void setValue(String value) {
        this.value = value;
        if (valueLabel != null) {
            valueLabel.setText(value);
        }
    }
}
