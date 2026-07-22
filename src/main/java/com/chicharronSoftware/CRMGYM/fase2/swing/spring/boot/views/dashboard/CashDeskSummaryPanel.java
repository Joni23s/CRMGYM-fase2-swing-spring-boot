package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * [MEJORA JUNIOR] Panel de Arqueo y Resumen de Caja del Día.
 * Permite al personal de recepción verificar los ingresos ingresados durante su turno
 * desglosados por medios de pago (Efectivo, Transferencia, Débito/MercadoPago).
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
        setLayout(new MigLayout("wrap 1, ins 18 20 18 20, fill", "[grow]", "[]12[grow]"));

        // Cabecera del Panel
        JLabel lblHeader = new JLabel("Arqueo de Caja del Día");
        lblHeader.setFont(Theme.FONT_SECTION_TITLE);
        lblHeader.setForeground(Theme.TEXT_ACTIVE);
        add(lblHeader, "growx");

        // Panel Interno de Contenido desglosado
        JPanel contentPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 10, fillx"));
        contentPanel.setOpaque(false);

        // Fila Total Destacada
        JPanel totalBox = createSummaryRow("Total Cobrado Hoy:", "$ 0,00", Theme.FONT_SECTION_TITLE, Color.decode("#059669"));
        lblTotalAmount = (JLabel) totalBox.getComponent(1);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_SLATE);

        // Desglose por Medio de Pago
        JPanel cashBox = createSummaryRow("💵 Efectivo:", "$ 0,00", Theme.FONT_BODY, Theme.TEXT_ACTIVE);
        lblCashAmount = (JLabel) cashBox.getComponent(1);

        JPanel transferBox = createSummaryRow("💳 Transferencia:", "$ 0,00", Theme.FONT_BODY, Theme.TEXT_ACTIVE);
        lblTransferAmount = (JLabel) transferBox.getComponent(1);

        JPanel debitBox = createSummaryRow("📱 Débito / MP:", "$ 0,00", Theme.FONT_BODY, Theme.TEXT_ACTIVE);
        lblDebitAmount = (JLabel) debitBox.getComponent(1);

        contentPanel.add(totalBox, "growx");
        contentPanel.add(sep, "growx, gaptop 4, gapbottom 4");
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
        lblValue.setFont(font);
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
        CardFactory.paintCardBackground(g, getWidth(), getHeight());
    }
}
