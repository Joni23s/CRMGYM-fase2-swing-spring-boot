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
 * [MEJORA EDITORIAL VINTAGE] Pantalla de Inicio (Dashboard POS Vintage).
 * - Tono Papel Envejecido (#EFE3C8), Tinta (#211B15), Rojo Sangre de Toro (#8C2320).
 * - Layout optimizado sin espacios verticales vacíos sobrantes en la columna derecha.
 * - Sin emojis Unicode corruptos.
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
        setLayout(new MigLayout("wrap 1, ins 16, fill", "[grow]", "[pref][pref][grow]"));

        // =====================================================================
        // 1. HEADER OPERATIVO (BUSCADOR EXPRÉS + BOTONES PLANOS VINTAGE)
        // =====================================================================
        JPanel topHeaderPanel = CardFactory.createCardPanel(new MigLayout("ins 10 14 10 14, fillx", "[grow]14[pref]8[pref]", "[center]"));

        // Buscador por DNI o Nombre (Sin emojis corruptos)
        txtQuickSearchDni = new JTextField();
        txtQuickSearchDni.setFont(new Font("Courier Prime", Font.PLAIN, 13));
        txtQuickSearchDni.putClientProperty("JTextField.placeholderText", "Buscar socio por DNI o Nombre (Presione Enter)...");
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

        // Botón Cobrar Cuota (Sangre de Toro / Arc = 0)
        btnQuickPay = new JButton("Cobrar Cuota");
        btnQuickPay.setFont(new Font("Oswald", Font.BOLD, 12));
        btnQuickPay.setBackground(Color.decode("#8C2320"));
        btnQuickPay.setForeground(Color.WHITE);
        btnQuickPay.setFocusPainted(false);
        btnQuickPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickPay.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToPayments();
        });

        // Botón Nuevo Socio (Tinta / Arc = 0)
        btnQuickNewClient = new JButton("+ Nuevo Socio");
        btnQuickNewClient.setFont(new Font("Oswald", Font.BOLD, 12));
        btnQuickNewClient.setBackground(Color.decode("#211B15"));
        btnQuickNewClient.setForeground(Color.WHITE);
        btnQuickNewClient.setFocusPainted(false);
        btnQuickNewClient.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickNewClient.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToClients();
        });

        topHeaderPanel.add(txtQuickSearchDni, "growx");
        topHeaderPanel.add(btnQuickPay, "h 34!");
        topHeaderPanel.add(btnQuickNewClient, "h 34!");

        add(topHeaderPanel, "growx, gapbottom 12");

        // =====================================================================
        // 2. SECCIÓN DE MÉTRICAS KPI (TARJETAS PLANAS PAPEL ENVEJECIDO)
        // =====================================================================
        JPanel kpiRow = new JPanel(new MigLayout("ins 0, fill, gapx 12", "[grow][grow]", "[pref]"));
        kpiRow.setOpaque(false);

        cardClients = new MetricCard(
                "Socios Activos",
                "0",
                "Socios vigentes en el gimnasio",
                "users",
                Theme.TEXT_INACTIVE,
                false,
                null);

        cardRevenue = new MetricCard(
                "Recaudación Total",
                "$ 0,00",
                "Ingresos acumulados confirmados",
                "dollar-sign",
                Theme.TEXT_INACTIVE,
                false,
                null);

        kpiRow.add(cardClients, "grow");
        kpiRow.add(cardRevenue, "grow");

        add(kpiRow, "growx, gapbottom 12");

        // =====================================================================
        // 3. GRILLA PRINCIPAL (TABLA 65% / VENCIMIENTOS Y CAJA 35% FIT CONTENT)
        // =====================================================================
        JPanel splitGrid = new JPanel(new MigLayout("ins 0, fill", "[65%, grow]12[35%, grow]", "[grow]"));
        splitGrid.setOpaque(false);

        // Tabla de Registro Reciente de Pagos
        tableModel = new DefaultTableModel(new String[]{"Cliente", "Membresía", "Fecha Pago", "Monto", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tableRecentPayments = new DashboardTable(tableModel);

        JPanel tableCard = CardFactory.createCardPanel(new MigLayout("wrap 1, ins 14 16 14 16, fill", "[grow]", "[]10[grow]"));

        JPanel tableHeaderPanel = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
        tableHeaderPanel.setOpaque(false);

        JLabel lblTableTitle = new JLabel("REGISTRO RECIENTE DE PAGOS");
        lblTableTitle.setFont(new Font("Oswald", Font.BOLD, 14));
        lblTableTitle.setForeground(Color.decode("#8C2320")); // Oxblood
        tableHeaderPanel.add(lblTableTitle, "left");

        JLabel lblVerHistorial = new JLabel("Ver historial completo ->");
        lblVerHistorial.setFont(new Font("Oswald", Font.BOLD, 11));
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
        scrollTable.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_SLATE));
        scrollTable.setOpaque(false);
        scrollTable.getViewport().setOpaque(false);
        tableCard.add(scrollTable, "grow");

        splitGrid.add(tableCard, "grow");

        // Columna Derecha (Ajustada dinámicamente a la altura de su contenido [pref])
        JPanel rightColumnPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 12, fillx", "[grow]", "[pref][pref]"));
        rightColumnPanel.setOpaque(false);

        expirationsPanel = new UpcomingExpirationsPanel();
        cashDeskPanel = new CashDeskSummaryPanel();

        rightColumnPanel.add(expirationsPanel, "growx");
        rightColumnPanel.add(cashDeskPanel, "growx");

        splitGrid.add(rightColumnPanel, "growx, aligny top");

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
