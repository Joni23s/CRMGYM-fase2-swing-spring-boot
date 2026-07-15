package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecentActivityPanel extends JPanel {

    private JPanel listPanel;

    public RecentActivityPanel() {
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 1, ins 18 20 18 20, fill", "[grow]", "[]10[grow]"));

        JPanel headerPanel = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
        headerPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("Actividad Reciente");
        lblHeader.setFont(Theme.FONT_SECTION_TITLE);
        lblHeader.setForeground(Theme.TEXT_ACTIVE);
        headerPanel.add(lblHeader, "left");

        JLabel lblLink = new JLabel("Ver todo");
        lblLink.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLink.setForeground(Theme.TEXT_INACTIVE);
        lblLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.add(lblLink, "right");

        add(headerPanel, "cell 0 0, growx");

        listPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 8, fillx"));
        listPanel.setOpaque(false);
        add(listPanel, "cell 0 1, grow");
    }

    public void updateData(List<PaymentDTO> recentPayments) {
        listPanel.removeAll();
        if (recentPayments == null || recentPayments.isEmpty()) {
            JLabel emptyLabel = new JLabel("Sin actividad reciente");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLabel.setForeground(Theme.TEXT_INACTIVE);
            listPanel.add(emptyLabel, "align center");
        } else {
            for (PaymentDTO p : recentPayments) {
                String name = p.getNameClient();
                String text = name + " pagó " + (p.getNamePlan() != null && !p.getNamePlan().isEmpty() ? p.getNamePlan() : "membresía");
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String timeText = "Fecha: " + p.getPaymentDate().format(formatter);
                
                Color iconColor = Color.decode("#10b981");
                Color badgeBg = new Color(16, 185, 129, 30);
                String iconType = "check";
                
                if ("PENDIENTE".equals(p.getPaymentStatus())) {
                    text = name + " tiene un pago pendiente";
                    iconColor = Color.decode("#f59e0b");
                    badgeBg = new Color(245, 158, 11, 30);
                    iconType = "credit-card";
                }
                
                listPanel.add(new ActivityItem(text, timeText, iconType, badgeBg, iconColor), "growx");
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

    private static class ActivityItem extends JPanel {

        public ActivityItem(String text, String timeText, String iconType,
                            Color badgeBgColor, Color iconColor) {
            setOpaque(false);
            setLayout(new MigLayout("ins 8 10 8 10, fill", "[32px!]10[grow]", "[grow]"));

            JPanel badge = new JPanel(new MigLayout("ins 0, align center, fill")) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(badgeBgColor);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            badge.setPreferredSize(new Dimension(32, 32));
            badge.setOpaque(false);

            JLabel lblIcon = new JLabel(new VectorIcon(iconType, 14, iconColor));
            badge.add(lblIcon, "align center");
            add(badge, "cell 0 0, aligny center");

            JPanel textPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 0"));
            textPanel.setOpaque(false);

            JLabel lblText = new JLabel(text);
            lblText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblText.setForeground(Theme.TEXT_ACTIVE);

            JLabel lblTime = new JLabel(timeText);
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblTime.setForeground(Theme.TEXT_INACTIVE);

            textPanel.add(lblText);
            textPanel.add(lblTime);
            add(textPanel, "cell 1 0, aligny center, growx");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Theme.CARD_BG_ALT);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2d.dispose();
        }
    }
}
