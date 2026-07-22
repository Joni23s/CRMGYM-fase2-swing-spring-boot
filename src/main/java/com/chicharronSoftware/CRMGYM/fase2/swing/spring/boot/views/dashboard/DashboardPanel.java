package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.DashboardTable;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.MetricCard;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.FormatterUtils;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class DashboardPanel extends JPanel implements Scrollable {

    private static final int BP_LARGE  = 950;
    private static final int BP_MEDIUM = 650;

    private enum LayoutMode { LARGE, MEDIUM, SMALL }

    public interface DashboardNavigationListener {
        void onNavigateToClients();
        void onNavigateToPayments();
        void onNavigateToHistory();
    }

    private DashboardNavigationListener navigationListener;

    public void setNavigationListener(DashboardNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    private DashboardTable     tableRecentPayments;
    private DefaultTableModel  tableModel;

    private UpcomingExpirationsPanel expirationsPanel;
    private RecentActivityPanel      activityPanel;

    private MetricCard cardClients;
    private MetricCard cardPlans;
    private MetricCard cardRevenue;

    private final ButtonFactory buttonFactory;

    public DashboardPanel(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
        initComponentsHandCoded();
    }

    private void initComponentsHandCoded() {
        setOpaque(true);
        setBackground(Theme.BG_DARK);
        setLayout(new MigLayout("wrap 1, ins 20, fill", "[grow]", "[pref][pref][grow][pref]"));

        final MigLayout dashboardLayout = (MigLayout) getLayout();

        // =====================================================================
        // FILA 1: TOPBAR (SALUDO + CÁPSULA DINÁMICA FECHA/CLIMA/PERFIL)
        // =====================================================================
        final MigLayout topBarLayout = new MigLayout("ins 0, fillx", "[grow]push[]");
        final JPanel topBar = new JPanel(topBarLayout);
        topBar.setOpaque(false);

        final JPanel welcomePanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 2"));
        welcomePanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("¡Hola, Administrador!");
        lblWelcome.setFont(Theme.FONT_DASHBOARD_TITLE);
        lblWelcome.setForeground(Theme.TEXT_ACTIVE);

        JLabel lblSubtitle = new JLabel("Resumen del estado de tu gimnasio");
        lblSubtitle.setFont(Theme.FONT_BODY);
        lblSubtitle.setForeground(Theme.TEXT_INACTIVE);

        welcomePanel.add(lblWelcome);
        welcomePanel.add(lblSubtitle);
        topBar.add(welcomePanel, "left");

        final JPanel weatherCapsule = new JPanel(new MigLayout("ins 4 12 4 12, aligny center, gapx 10")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Theme.CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.ARC_CAPSULE, Theme.ARC_CAPSULE);
                g2d.setColor(Theme.BORDER_SLATE);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.ARC_CAPSULE, Theme.ARC_CAPSULE);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        weatherCapsule.setOpaque(false);

        JLabel iconSun = new JLabel(new VectorIcon("sun", 18));

        JPanel weatherTextPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 0"));
        weatherTextPanel.setOpaque(false);
        JLabel lblWeather = new JLabel("Soleado, 18°C");
        lblWeather.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblWeather.setForeground(Theme.TEXT_ACTIVE);
        JLabel lblCity = new JLabel("Palmira, Valle");
        lblCity.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblCity.setForeground(Theme.TEXT_INACTIVE);
        weatherTextPanel.add(lblWeather);
        weatherTextPanel.add(lblCity);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setForeground(Theme.BORDER_SLATE);
        sep.setBackground(Theme.BORDER_SLATE);

        LocalDate today = LocalDate.now();
        DateTimeFormatter dayFormatter   = DateTimeFormatter.ofPattern("EEEE, d 'de'",  Locale.forLanguageTag("es-AR"));
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM",          Locale.forLanguageTag("es-AR"));
        DateTimeFormatter yearFormatter  = DateTimeFormatter.ofPattern("yyyy",           Locale.forLanguageTag("es-AR"));

        String dayPart   = today.format(dayFormatter);
        String monthPart = today.format(monthFormatter);
        String yearPart  = today.format(yearFormatter);
        dayPart   = dayPart.substring(0, 1).toUpperCase() + dayPart.substring(1);
        monthPart = monthPart.substring(0, 1).toUpperCase() + monthPart.substring(1);

        JLabel lblDate = new JLabel(dayPart + " " + monthPart + " " + yearPart);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(Theme.TEXT_INACTIVE);

        JSeparator sep2 = new JSeparator(JSeparator.VERTICAL);
        sep2.setForeground(Theme.BORDER_SLATE);
        sep2.setBackground(Theme.BORDER_SLATE);

        JPanel miniAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.decode("#cbd5e1"));
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.setPaint(new GradientPaint(0, 0, Color.decode("#3b82f6"), getWidth(), getHeight(), Color.decode("#1d4ed8")));
                g2d.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2d.setColor(Color.WHITE);
                int w = getWidth(); int h = getHeight();
                g2d.fillOval(w / 4, h / 5, w / 2, h / 2 - 1);
                g2d.fillArc(w / 8, h * 3 / 5, w * 3 / 4, h * 2 / 3, 0, 180);
                g2d.dispose();
            }
        };
        miniAvatar.setPreferredSize(new Dimension(28, 28));
        miniAvatar.setOpaque(false);

        JLabel lblMiniChevron = new JLabel("v");
        lblMiniChevron.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblMiniChevron.setForeground(Theme.TEXT_INACTIVE);

        weatherCapsule.add(iconSun);
        weatherCapsule.add(weatherTextPanel);
        weatherCapsule.add(sep,  "w 1!, h 20!, aligny center");
        weatherCapsule.add(lblDate, "aligny center");
        weatherCapsule.add(sep2, "w 1!, h 20!, aligny center");
        weatherCapsule.add(miniAvatar, "w 28!, h 28!, aligny center");
        weatherCapsule.add(lblMiniChevron, "aligny center");

        topBar.add(weatherCapsule, "right");
        add(topBar, "growx, gapbottom 16");

        // =====================================================================
        // FILA 2: SECCIÓN DE MÉTRICAS KPI
        // =====================================================================
        final MigLayout kpiRowLayout = new MigLayout("ins 0, fill, gapx 16", "[grow][grow][grow]", "[pref]");
        final JPanel kpiRow = new JPanel(kpiRowLayout);
        kpiRow.setOpaque(false);

        cardClients = new MetricCard(
                "Clientes Activos",
                "0",
                "+12% vs mes anterior",
                "users",
                Theme.STATUS_SUCCESS,
                true,
                new int[]{ 10, 12, 14, 13, 16, 18, 20 });

        cardPlans = new MetricCard(
                "Planes Habilitados",
                "0",
                "Todos vigentes",
                "file-text",
                Theme.TEXT_INACTIVE,
                false,
                null);

        cardRevenue = new MetricCard(
                "Recaudación Total",
                "$ 0.00",
                "+8% vs mes anterior",
                "dollar",
                Theme.STATUS_SUCCESS,
                true,
                new int[]{ 30, 35, 40, 38, 45, 50, 52 });

        kpiRow.add(cardClients, "grow");
        kpiRow.add(cardPlans,   "grow");
        kpiRow.add(cardRevenue, "grow");

        add(kpiRow, "growx, gapbottom 16");

        // =====================================================================
        // FILA 3: SPLIT GRID (65% TABLA | 35% COLUMNA DERECHA)
        // =====================================================================
        final MigLayout splitGridLayout = new MigLayout("ins 0, fill", "[65%, grow]16[35%, grow]", "[grow]");
        final JPanel splitGrid = new JPanel(splitGridLayout);
        splitGrid.setOpaque(false);

        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableModel.setColumnIdentifiers(new Object[]{"Cliente", "Membresía", "Fecha Pago", "Monto", "Estado"});
        tableRecentPayments = new DashboardTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tableRecentPayments);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.CARD_BG);

        final JPanel tableCard = CardFactory.createCardPanel(new MigLayout("fill, ins 16"));

        JLabel lblTableTitle = new JLabel("Registro Reciente de Pagos");
        lblTableTitle.setFont(Theme.FONT_SECTION_TITLE);
        lblTableTitle.setForeground(Theme.TEXT_ACTIVE);

        tableCard.add(lblTableTitle, "wrap, gapbottom 12");
        tableCard.add(scrollPane, "grow");
        splitGrid.add(tableCard, "grow");

        final JPanel rightColumnPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 16, fill", "[grow]", "[grow][grow]"));
        rightColumnPanel.setOpaque(false);

        expirationsPanel = new UpcomingExpirationsPanel();
        activityPanel    = new RecentActivityPanel();

        rightColumnPanel.add(expirationsPanel, "grow");
        rightColumnPanel.add(activityPanel,    "grow");
        splitGrid.add(rightColumnPanel, "grow");

        add(splitGrid, "grow, gapbottom 12");

        // =====================================================================
        // FILA 4: FOOTER DE ACCIONES RÁPIDAS
        // =====================================================================
        final MigLayout bottomActionsCardLayout = new MigLayout(
                "ins 12 24 12 24, fill, aligny center", "[grow][grow][grow]", "[]");
        final JPanel bottomActionsCard = CardFactory.createCardPanel(bottomActionsCardLayout);

        JButton btnRegisterSocio = new JButton("Registrar Nuevo Socio",
                new VectorIcon("user-plus", 16, Color.decode("#3b82f6")));
        JButton btnCobrar        = new JButton("Cobrar Membresía",
                new VectorIcon("credit-card", 16, Color.decode("#10b981")));
        JButton btnViewHistory   = new JButton("Ver Historial Completo",
                new VectorIcon("history", 16, Color.decode("#8b5cf6")));

        buttonFactory.styleToolActionButton(btnRegisterSocio);
        buttonFactory.styleToolActionButton(btnCobrar);
        buttonFactory.styleToolActionButton(btnViewHistory);

        btnRegisterSocio.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToClients();
        });
        btnCobrar.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToPayments();
        });
        btnViewHistory.addActionListener(e -> {
            if (navigationListener != null) navigationListener.onNavigateToHistory();
        });

        bottomActionsCard.add(btnRegisterSocio, "align center");
        bottomActionsCard.add(btnCobrar,        "align center");
        bottomActionsCard.add(btnViewHistory,   "align center");

        add(bottomActionsCard, "growx");

        // =====================================================================
        // LISTENER RESPONSIVE DINÁMICO (3 BREAKPOINTS)
        // =====================================================================
        addComponentListener(new java.awt.event.ComponentAdapter() {
            private LayoutMode currentMode = null;

            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();

                LayoutMode targetMode;
                if (width >= BP_LARGE) {
                    targetMode = LayoutMode.LARGE;
                } else if (width >= BP_MEDIUM) {
                    targetMode = LayoutMode.MEDIUM;
                } else {
                    targetMode = LayoutMode.SMALL;
                }

                if (currentMode == targetMode) return;
                currentMode = targetMode;
                removeAll();

                switch (targetMode) {
                    case LARGE: {
                        dashboardLayout.setLayoutConstraints("wrap 1, ins 20, fill");
                        dashboardLayout.setColumnConstraints("[grow]");
                        dashboardLayout.setRowConstraints("[pref][pref][grow][pref]");

                        topBarLayout.setLayoutConstraints("ins 0, fillx");
                        topBarLayout.setColumnConstraints("[grow]push[]");
                        topBar.removeAll();
                        topBar.add(welcomePanel, "left");
                        topBar.add(weatherCapsule, "right");

                        kpiRowLayout.setLayoutConstraints("ins 0, fill, gapx 16");
                        kpiRowLayout.setColumnConstraints("[grow][grow][grow]");
                        kpiRow.removeAll();
                        kpiRow.add(cardClients, "grow");
                        kpiRow.add(cardPlans,   "grow");
                        kpiRow.add(cardRevenue, "grow");

                        splitGridLayout.setLayoutConstraints("ins 0, fill");
                        splitGridLayout.setColumnConstraints("[65%, grow]16[35%, grow]");
                        splitGridLayout.setRowConstraints("[grow]");
                        splitGrid.removeAll();
                        splitGrid.add(tableCard,        "grow");
                        splitGrid.add(rightColumnPanel, "grow");

                        bottomActionsCardLayout.setLayoutConstraints("ins 12 24 12 24, fill, aligny center");
                        bottomActionsCardLayout.setColumnConstraints("[grow][grow][grow]");

                        add(topBar,            "growx, gapbottom 16");
                        add(kpiRow,            "growx, gapbottom 16");
                        add(splitGrid,         "grow, gapbottom 12");
                        add(bottomActionsCard, "growx");
                        break;
                    }
                    case MEDIUM: {
                        dashboardLayout.setLayoutConstraints("wrap 1, ins 16, fill");
                        dashboardLayout.setColumnConstraints("[grow]");
                        dashboardLayout.setRowConstraints("[pref][pref][pref][grow][pref]");

                        topBarLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 8");
                        topBarLayout.setColumnConstraints("[grow]");
                        topBar.removeAll();
                        topBar.add(welcomePanel,   "growx");
                        topBar.add(weatherCapsule, "alignx right");

                        kpiRowLayout.setLayoutConstraints("ins 0, fillx, gapx 12, gapy 12");
                        kpiRowLayout.setColumnConstraints("[grow][grow]");
                        kpiRow.removeAll();
                        kpiRow.add(cardClients, "grow");
                        kpiRow.add(cardPlans,   "grow");
                        kpiRow.add(cardRevenue, "grow, span 2");

                        splitGridLayout.setLayoutConstraints("ins 0, fill");
                        splitGridLayout.setColumnConstraints("[55%, grow]12[45%, grow]");
                        splitGridLayout.setRowConstraints("[grow]");
                        splitGrid.removeAll();
                        splitGrid.add(tableCard,        "grow");
                        splitGrid.add(rightColumnPanel, "grow");

                        bottomActionsCardLayout.setLayoutConstraints("ins 12 16 12 16, fillx, aligny center, gapx 8");
                        bottomActionsCardLayout.setColumnConstraints("[grow][grow][grow]");

                        add(topBar,            "growx, gapbottom 12");
                        add(kpiRow,            "growx, gapbottom 12");
                        add(splitGrid,         "grow, gapbottom 10");
                        add(bottomActionsCard, "growx");
                        break;
                    }
                    case SMALL: {
                        dashboardLayout.setLayoutConstraints("wrap 1, ins 12, fillx, gapy 12");
                        dashboardLayout.setColumnConstraints("[grow, fill]");
                        dashboardLayout.setRowConstraints("");

                        topBarLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 6");
                        topBarLayout.setColumnConstraints("[grow]");
                        topBar.removeAll();
                        topBar.add(welcomePanel,   "growx");
                        topBar.add(weatherCapsule, "alignx right");

                        kpiRowLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 10");
                        kpiRowLayout.setColumnConstraints("[grow, fill]");
                        kpiRow.removeAll();
                        kpiRow.add(cardClients, "growx");
                        kpiRow.add(cardPlans,   "growx");
                        kpiRow.add(cardRevenue, "growx");

                        splitGridLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 12");
                        splitGridLayout.setColumnConstraints("[grow, fill]");
                        splitGridLayout.setRowConstraints("");
                        splitGrid.removeAll();
                        splitGrid.add(tableCard,        "growx");
                        splitGrid.add(rightColumnPanel, "growx");

                        bottomActionsCardLayout.setLayoutConstraints("wrap 1, ins 10 14 10 14, fillx, gapy 8");
                        bottomActionsCardLayout.setColumnConstraints("[grow, fill]");

                        add(topBar,            "growx");
                        add(kpiRow,            "growx");
                        add(splitGrid,         "growx");
                        add(bottomActionsCard, "growx");
                        break;
                    }
                }

                revalidate();
                repaint();
            }
        });
    }

    public void updateMetrics(String activeClients, String activePlans, String revenue) {
        if (cardClients != null) cardClients.setValue(activeClients);
        if (cardPlans != null) cardPlans.setValue(activePlans);
        if (cardRevenue != null) cardRevenue.setValue(revenue);
    }

    public void updateRecentPaymentsTable(List<PaymentDTO> recentPayments) {
        tableModel.setRowCount(0);
        if (recentPayments != null) {
            for (PaymentDTO dto : recentPayments) {
                tableModel.addRow(new Object[]{
                        dto.getNameClient(),
                        dto.getNamePlan(),
                        FormatterUtils.formatDate(dto.getPaymentDate()),
                        FormatterUtils.formatCurrency(dto.getFinalAmount()),
                        dto.getPaymentStatus()
                });
            }
        }
    }

    public void updateUpcomingExpirations(List<PaymentDTO> pending) {
        expirationsPanel.updateData(pending);
    }

    public void updateRecentActivity(List<PaymentDTO> recent) {
        activityPanel.updateData(recent);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        return 80;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return getPreferredSize().height <= ((JViewport) getParent()).getHeight();
        }
        return false;
    }
}
