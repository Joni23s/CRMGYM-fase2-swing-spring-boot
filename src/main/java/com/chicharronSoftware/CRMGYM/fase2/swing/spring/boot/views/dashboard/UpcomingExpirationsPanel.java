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
 * [MEJORA EDITORIAL VINTAGE] Panel de Próximos Vencimientos.
 * La altura responde estrictamente a la cantidad de ítems [pref], evitando espacios vacíos verticales.
 * Avatares recoloreados con la paletaVintage (#211B15, #8C2320, #2E4057, #C68A1E).
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
        // [MEJORA UI] Layout compacto [pref] para que la altura responda exactamente al contenido
        setLayout(new MigLayout("wrap 1, ins 14 16 14 16, fillx", "[grow]", "[]8[pref]"));

        JPanel headerPanel = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
        headerPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("PRÓXIMOS VENCIMIENTOS");
        lblHeader.setFont(new Font("Oswald", Font.BOLD, 14));
        lblHeader.setForeground(Color.decode("#8C2320")); // Oxblood Header
        headerPanel.add(lblHeader, "left");

        add(headerPanel, "cell 0 0, growx");

        listPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 6, fillx"));
        listPanel.setOpaque(false);
        add(listPanel, "cell 0 1, growx");
    }

    public void updateData(List<PaymentDTO> pendingPayments) {
        listPanel.removeAll();
        if (pendingPayments == null || pendingPayments.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hay pagos pendientes");
            emptyLabel.setFont(new Font("Courier Prime", Font.PLAIN, 12));
            emptyLabel.setForeground(Theme.TEXT_INACTIVE);
            listPanel.add(emptyLabel, "align center, py 10");
        } else {
            int index = 0;
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
                Color alertColor = Color.decode("#8C5E12"); // Mostaza para pendiente

                if ("VENCIDO".equals(p.getPaymentStatus())) {
                    alertText = "VENCIDO";
                    alertColor = Color.decode("#8C2320"); // Oxblood para vencido
                }

                // Paleta de avatares: Tinta (#211B15), Oxblood (#8C2320), Azul Apagado (#2E4057), Mostaza (#C68A1E)
                Color[] avatarPalette = new Color[]{
                        Color.decode("#211B15"),
                        Color.decode("#8C2320"),
                        Color.decode("#2E4057"),
                        Color.decode("#C68A1E")
                };
                Color avatarBg = avatarPalette[index % avatarPalette.length];
                index++;

                ExpirationItem item = new ExpirationItem(p, name, plan, alertText, initials, avatarBg, alertColor, cobrarClickListener);
                listPanel.add(item, "growx");
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Theme.enableHighFidelity(g2d);

        int w = getWidth();
        int h = getHeight();

        // Fondo plano de papel
        g2d.setColor(Theme.CARD_BG);
        g2d.fillRect(0, 0, w, h);

        // Borde recto 1px de tinta
        g2d.setColor(Theme.BORDER_SLATE);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRect(0, 0, w - 1, h - 1);

        g2d.dispose();
    }

    private static class ExpirationItem extends JPanel {

        public ExpirationItem(PaymentDTO payment, String name, String planName, String alertText,
                              String initials, Color avatarBgColor, Color alertTextColor,
                              OnCobrarClickListener listener) {
            setOpaque(false);
            setLayout(new MigLayout("ins 6 8 6 8, fillx", "[28px!]8[grow][pref]6[pref]", "[center]"));

            JPanel badge = new JPanel(new MigLayout("ins 0, align center, fill")) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    Theme.enableHighFidelity(g2d);
                    g2d.setColor(avatarBgColor);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Oswald", Font.BOLD, 10));
                    FontMetrics fm = g2d.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(initials)) / 2;
                    int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2d.drawString(initials, tx, ty);
                    g2d.dispose();
                }
            };
            badge.setPreferredSize(new Dimension(28, 28));
            badge.setOpaque(false);

            JPanel textPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 0"));
            textPanel.setOpaque(false);
            JLabel lblName = new JLabel(name);
            lblName.setFont(new Font("Courier Prime", Font.BOLD, 12));
            lblName.setForeground(Theme.TEXT_ACTIVE);
            JLabel lblPlan = new JLabel(planName);
            lblPlan.setFont(new Font("Courier Prime", Font.PLAIN, 10));
            lblPlan.setForeground(Theme.TEXT_INACTIVE);
            textPanel.add(lblName);
            textPanel.add(lblPlan);

            JLabel lblAlert = new JLabel(alertText.toUpperCase());
            lblAlert.setFont(new Font("Oswald", Font.BOLD, 10));
            lblAlert.setForeground(alertTextColor);

            // Botón Cobrar con Estilo Plano (Arc = 0) y Rojo Sangre de Toro
            JButton btnCobrarInLine = new JButton("Cobrar ($)");
            btnCobrarInLine.setFont(new Font("Oswald", Font.BOLD, 11));
            btnCobrarInLine.setBackground(Color.decode("#8C2320"));
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
            add(btnCobrarInLine, "cell 3 0, aligny center, h 26!");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            Theme.enableHighFidelity(g2d);
            g2d.setColor(Theme.CARD_BG_ALT);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2d.dispose();
        }
    }
}
