package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * [MEJORA EDITORIAL VINTAGE] Panel de Arqueo y Resumen de Caja del Día.
 * Presentación limpia sin caracteres emoji corruptos.
 */
public class CashDeskSummaryPanel extends JPanel {

    private JLabel lblTotalAmount;
    private JLabel lblCashAmount;
    private JLabel lblTransferAmount;
    private JLabel lblDebitAmount;

    public CashDeskSummaryPanel() {
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 1, ins 14 16 14 16, fill", "[grow]", "[]8[grow]"));

        // Cabecera del Panel en Oxblood y Oswald Mayúsculas
        JLabel lblHeader = new JLabel("ARQUEO DE CAJA DEL DÍA");
        lblHeader.setFont(new Font("Oswald", Font.BOLD, 14));
        lblHeader.setForeground(Color.decode("#8C2320"));
        add(lblHeader, "growx");

        // Panel Interno de Contenido desglosado
        JPanel contentPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 8, fillx"));
        contentPanel.setOpaque(false);

        // Fila Total Destacada
        JPanel totalBox = createSummaryRow("TOTAL COBRADO HOY:", "$ 0,00", new Font("Oswald", Font.BOLD, 13), Color.decode("#8C2320"));
        lblTotalAmount = (JLabel) totalBox.getComponent(1);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_SLATE);

        // Desglose por Medio de Pago (Sin emojisUnicode)
        JPanel cashBox = createSummaryRow("Efectivo:", "$ 0,00", new Font("Courier Prime", Font.PLAIN, 12), Theme.TEXT_ACTIVE);
        lblCashAmount = (JLabel) cashBox.getComponent(1);

        JPanel transferBox = createSummaryRow("Transferencia:", "$ 0,00", new Font("Courier Prime", Font.PLAIN, 12), Theme.TEXT_ACTIVE);
        lblTransferAmount = (JLabel) transferBox.getComponent(1);

        JPanel debitBox = createSummaryRow("Débito / MP:", "$ 0,00", new Font("Courier Prime", Font.PLAIN, 12), Theme.TEXT_ACTIVE);
        lblDebitAmount = (JLabel) debitBox.getComponent(1);

        contentPanel.add(totalBox, "growx");
        contentPanel.add(sep, "growx, gaptop 2, gapbottom 2");
        contentPanel.add(cashBox, "growx");
        contentPanel.add(transferBox, "growx");
        contentPanel.add(debitBox, "growx");

        add(contentPanel, "grow");
    }

    private JPanel createSummaryRow(String labelText, String defaultValue, Font font, Color valueColor) {
        JPanel row = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
        row.setOpaque(false);

        JLabel lblTitle = new JLabel(labelText);
        lblTitle.setFont(font);
        lblTitle.setForeground(Theme.TEXT_INACTIVE);

        JLabel lblValue = new JLabel(defaultValue);
        lblValue.setFont(new Font("Courier Prime", Font.BOLD, 13));
        lblValue.setForeground(valueColor);

        row.add(lblTitle, "left");
        row.add(lblValue, "right");
        return row;
    }

    public void updateCashSummary(String total, String cash, String transfer, String debit) {
        if (lblTotalAmount != null) lblTotalAmount.setText(total != null ? total : "$ 0,00");
        if (lblCashAmount != null) lblCashAmount.setText(cash != null ? cash : "$ 0,00");
        if (lblTransferAmount != null) lblTransferAmount.setText(transfer != null ? transfer : "$ 0,00");
        if (lblDebitAmount != null) lblDebitAmount.setText(debit != null ? debit : "$ 0,00");
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

        // Borde recto 1px de Tinta
        g2d.setColor(Theme.BORDER_SLATE);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRect(0, 0, w - 1, h - 1);

        g2d.dispose();
    }
}
