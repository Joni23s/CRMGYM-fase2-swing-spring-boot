package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * [MEJORA JUNIOR] Panel de Próximos Vencimientos con Acciones Directas en Línea (In-Line Actions).
 * Permite al recepcionista cobrar una cuota vencida o próxima a vencer en 1 solo clic.
 */
public class UpcomingExpirationsPanel extends JPanel {

    public interface OnCobrarClickListener {
        void onCobrar(PaymentDTO payment);
    }

    private JPanel listPanel;
    private OnCobrarClickListener cobrarClickListener;

    public UpcomingExpirationsPanel() {
        setOpaque(false);
        initComponents();
    }

    public void setOnCobrarClickListener(OnCobrarClickListener listener) {
        this.cobrarClickListener = listener;
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 1, ins 18 20 18 20, fill", "[grow]", "[]10[grow]"));

        JPanel headerPanel = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
        headerPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("Próximos Vencimientos");
        lblHeader.setFont(Theme.FONT_SECTION_TITLE);
        lblHeader.setForeground(Theme.TEXT_ACTIVE);
        headerPanel.add(lblHeader, "left");

        add(headerPanel, "cell 0 0, growx");

        listPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 8, fillx"));
        listPanel.setOpaque(false);
        add(listPanel, "cell 0 1, grow");
    }

    public void updateData(List<PaymentDTO> pendingPayments) {
        listPanel.removeAll();
        if (pendingPayments == null || pendingPayments.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hay pagos pendientes");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLabel.setForeground(Theme.TEXT_INACTIVE);
            listPanel.add(emptyLabel, "align center");
        } else {
            for (PaymentDTO p : pendingPayments) {
                String name = p.getNameClient();
                String plan = p.getNamePlan() != null && !p.getNamePlan().isEmpty() ? p.getNamePlan() : "Sin Plan";

                String initials = "";
                if (name != null && !name.trim().isEmpty()) {
                    String[] parts = name.trim().split("\\s+");
                    if (parts.length >= 2) {
                        initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
                    } else if (parts[0].length() >= 2) {
                        initials = parts[0].substring(0, 2).toUpperCase();
                    } else {
                        initials = parts[0].toUpperCase();
                    }
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                String alertText = "Vence: " + (p.getPeriod() != null ? p.getPeriod().format(formatter) : "-");
                Color alertColor = Color.decode("#f59e0b");
                Color badgeBg = new Color(245, 158, 11, 30);

                if ("VENCIDO".equals(p.getPaymentStatus())) {
                    alertText = "VENCIDO";
                    alertColor = Color.decode("#ef4444");
                    badgeBg = new Color(239, 68, 68, 30);
                }

                ExpirationItem item = new ExpirationItem(p, name, plan, alertText, initials, badgeBg, alertColor, cobrarClickListener);
                listPanel.add(item, "growx");
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        CardFactory.paintCardBackground(g, getWidth(), getHeight());
    }

    private static class ExpirationItem extends JPanel {

        public ExpirationItem(PaymentDTO payment, String name, String planName, String alertText,
                              String initials, Color badgeBgColor, Color alertTextColor,
                              OnCobrarClickListener listener) {
            setOpaque(false);
            setLayout(new MigLayout("ins 8 10 8 10, fill", "[32px!]10[grow][pref]8[pref]", "[grow]"));

            JPanel badge = new JPanel(new MigLayout("ins 0, align center, fill")) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g2d.setColor(badgeBgColor);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g2d.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(initials)) / 2;
                    int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2d.drawString(initials, tx, ty);
                    g2d.dispose();
                }
            };
            badge.setPreferredSize(new Dimension(32, 32));
            badge.setOpaque(false);

            JPanel textPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 0"));
            textPanel.setOpaque(false);
            JLabel lblName = new JLabel(name);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblName.setForeground(Theme.TEXT_ACTIVE);
            JLabel lblPlan = new JLabel(planName);
            lblPlan.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblPlan.setForeground(Theme.TEXT_INACTIVE);
            textPanel.add(lblName);
            textPanel.add(lblPlan);

            JLabel lblAlert = new JLabel(alertText);
            lblAlert.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblAlert.setForeground(alertTextColor);

            JButton btnCobrarInLine = new JButton("Cobrar ($)");
            btnCobrarInLine.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnCobrarInLine.setBackground(Color.decode("#059669"));
            btnCobrarInLine.setForeground(Color.WHITE);
            btnCobrarInLine.setFocusPainted(false);
            btnCobrarInLine.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCobrarInLine.addActionListener(e -> {
                if (listener != null) {
                    listener.onCobrar(payment);
                }
            });

            add(badge, "cell 0 0, aligny center");
            add(textPanel, "cell 1 0, aligny center");
            add(lblAlert, "cell 2 0, aligny center");
            add(btnCobrarInLine, "cell 3 0, aligny center");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Theme.CARD_BG_ALT);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            g2d.dispose();
        }
    }
}
