package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.DashboardTable;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.MetricCard;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * [MEJORA JUNIOR] Pantalla de Inicio (Dashboard Operativo POS).
 * Rediseñada bajo el concepto de software de escritorio para personal de recepción:
 * - Header Superior con Buscador Exprés por DNI y Botones de Acción Primarios.
 * - Tarjetas KPI simplificadas (Socios Activos y Recaudación Total).
 * - Grilla Principal con Registro de Pagos Recientes, Próximos Vencimientos (acciones directas)
 *   y Arqueo de Caja del Día desglosado.
 */
@Component
public class DashboardPanel extends JPanel implements Scrollable {

    public interface DashboardNavigationListener {
        void onNavigateToClients();
        void onNavigateToPayments();
        void onNavigateToHistory();
        void onQuickSearchDni(String query);
        void onCobrarSpecificClient(PaymentDTO payment);
    }

    private DashboardNavigationListener navigationListener;

    public void setNavigationListener(DashboardNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
        if (expirationsPanel != null) {
            expirationsPanel.setOnCobrarClickListener(payment -> {
                if (this.navigationListener != null) {
                    this.navigationListener.onCobrarSpecificClient(payment);
                }
            });
        }
    }

    private DashboardTable tableRecentPayments;
    private DefaultTableModel tableModel;

    private UpcomingExpirationsPanel expirationsPanel;
    private CashDeskSummaryPanel cashDeskPanel;

    private MetricCard cardClients;
    private MetricCard cardRevenue;

    private JTextField txtQuickSearchDni;
    private JButton btnQuickPay;
    private JButton btnQuickNewClient;

    private final ButtonFactory buttonFactory;

    public DashboardPanel(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
        initComponentsHandCoded();
    }

    private void initComponentsHandCoded() {
        setOpaque(true);
        setBackground(Theme.BG_DARK);
        setLayout(new MigLayout("wrap 1, ins 20, fill", "[grow]", "[pref][pref][grow]"));

        // =====================================================================
        // 1. HEADER OPERATIVO (BUSCADOR EXPRÉS + BOTONES PRIMARIOS)
        // =====================================================================
        JPanel topHeaderPanel = CardFactory.createCardPanel(new MigLayout("ins 12 18 12 18, fillx", "[grow]20[pref]10[pref]", "[center]"));

        // Buscador por DNI o Nombre
        txtQuickSearchDni = new JTextField();
        txtQuickSearchDni.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQuickSearchDni.putClientProperty("JTextField.placeholderText", "🔍 Buscar socio por DNI o Nombre (Presione Enter)...");
        txtQuickSearchDni.putClientProperty("JTextField.selectAllOnFocus", true);
        txtQuickSearchDni.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String query = txtQuickSearchDni.getText().trim();
                    if (!query.isEmpty() && navigationListener != null) {
                        navigationListener.onQuickSearchDni(query);
                    }
                }
            }
        });

        // Botón Cobrar Cuota (Verde Esmeralda Operativo)
        btnQuickPay = new JButton("💳 Cobrar Cuota");
        btnQuickPay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQuickPay.setBackground(Color.decode("#059669"));
        btnQuickPay.setForeground(Color.WHITE);
        btnQuickPay.setFocusPainted(false);
        btnQuickPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickPay.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToPayments();
        });

        // Botón Nuevo Socio (Azul Corporativo)
        btnQuickNewClient = new JButton("+ Nuevo Socio");
        btnQuickNewClient.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQuickNewClient.setBackground(Color.decode("#2563eb"));
        btnQuickNewClient.setForeground(Color.WHITE);
        btnQuickNewClient.setFocusPainted(false);
        btnQuickNewClient.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickNewClient.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToClients();
        });

        topHeaderPanel.add(txtQuickSearchDni, "growx");
        topHeaderPanel.add(btnQuickPay, "h 36!");
        topHeaderPanel.add(btnQuickNewClient, "h 36!");

        add(topHeaderPanel, "growx, gapbottom 14");

        // =====================================================================
        // 2. SECCIÓN DE MÉTRICAS KPI (2 TARJETAS SIMPLIFICADAS Y CLARAS)
        // =====================================================================
        JPanel kpiRow = new JPanel(new MigLayout("ins 0, fill, gapx 14", "[grow][grow]", "[pref]"));
        kpiRow.setOpaque(false);

        cardClients = new MetricCard(
                "Socios Activos",
                "0",
                "Socios vigentes en el gimnasio",
                "users",
                Theme.STATUS_SUCCESS,
                false,
                null);

        cardRevenue = new MetricCard(
                "Recaudación Total",
                "$ 0,00",
                "Ingresos acumulados confirmados",
                "dollar-sign",
                Theme.ACCENT_CYAN,
                false,
                null);

        kpiRow.add(cardClients, "grow");
        kpiRow.add(cardRevenue, "grow");

        add(kpiRow, "growx, gapbottom 14");

        // =====================================================================
        // 3. GRILLA PRINCIPAL (65% REGISTRO DE PAGOS / 35% VENCIMIENTOS Y CAJA)
        // =====================================================================
        JPanel splitGrid = new JPanel(new MigLayout("ins 0, fill", "[65%, grow]14[35%, grow]", "[grow]"));
        splitGrid.setOpaque(false);

        // Tabla de Registro Reciente de Pagos
        tableModel = new DefaultTableModel(new String[]{"Cliente", "Membresía", "Fecha Pago", "Monto", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tableRecentPayments = new DashboardTable(tableModel);

        JPanel tableCard = CardFactory.createCardPanel(new MigLayout("wrap 1, ins 18 20 18 20, fill", "[grow]", "[]12[grow]"));

        JPanel tableHeaderPanel = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
        tableHeaderPanel.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Registro Reciente de Pagos");
        lblTableTitle.setFont(Theme.FONT_SECTION_TITLE);
        lblTableTitle.setForeground(Theme.TEXT_ACTIVE);
        tableHeaderPanel.add(lblTableTitle, "left");

        JLabel lblVerHistorial = new JLabel("Ver historial completo");
        lblVerHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVerHistorial.setForeground(Theme.TEXT_INACTIVE);
        lblVerHistorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblVerHistorial.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (navigationListener != null) navigationListener.onNavigateToHistory();
            }
        });
        tableHeaderPanel.add(lblVerHistorial, "right");

        tableCard.add(tableHeaderPanel, "growx");

        JScrollPane scrollTable = new JScrollPane(tableRecentPayments);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.setOpaque(false);
        scrollTable.getViewport().setOpaque(false);
        tableCard.add(scrollTable, "grow");

        splitGrid.add(tableCard, "grow");

        // Columna Derecha (Vencimientos + Arqueo de Caja)
        JPanel rightColumnPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 14, fill", "[grow]", "[grow][pref]"));
        rightColumnPanel.setOpaque(false);

        expirationsPanel = new UpcomingExpirationsPanel();
        cashDeskPanel = new CashDeskSummaryPanel();

        rightColumnPanel.add(expirationsPanel, "grow");
        rightColumnPanel.add(cashDeskPanel, "growx");

        splitGrid.add(rightColumnPanel, "grow");

        add(splitGrid, "grow");
    }

    // =========================================================================
    // MÉTODOS DE ACTUALIZACIÓN DE UI DESDE EL PRESENTER
    // =========================================================================
    public void updateMetrics(String activeClientsCount, String totalRevenueFormatted) {
        if (cardClients != null) cardClients.setValue(activeClientsCount);
        if (cardRevenue != null) cardRevenue.setValue(totalRevenueFormatted);
    }

    public void updateRecentPaymentsTable(List<PaymentDTO> payments) {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        if (payments != null) {
            for (PaymentDTO p : payments) {
                tableModel.addRow(new Object[]{
                        p.getNameClient(),
                        p.getNamePlan() != null ? p.getNamePlan() : "Sin Plan",
                        p.getPaymentDate() != null ? p.getPaymentDate().toString() : "-",
                        p.getFinalAmount() != null ? "$ " + p.getFinalAmount() : "$ 0,00",
                        p.getPaymentStatus() != null ? p.getPaymentStatus() : "PENDIENTE"
                });
            }
        }
    }

    public void updateUpcomingExpirations(List<PaymentDTO> pendingPayments) {
        if (expirationsPanel != null) {
            expirationsPanel.updateData(pendingPayments);
        }
    }

    public void updateCashDeskSummary(String total, String cash, String transfer, String debit) {
        if (cashDeskPanel != null) {
            cashDeskPanel.updateCashSummary(total, cash, transfer, debit);
        }
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE Scrollable
    // =========================================================================
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 80;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }
}
