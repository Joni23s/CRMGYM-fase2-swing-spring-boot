package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.MainFrame;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.DashboardTable;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.MetricCard;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.FormatterUtils;

import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * =================================================================================
 * GUÍA DE ARQUITECTURA SÓLIDA: SEPARACIÓN DE RESPONSABILIDADES Y CONTENEDORES
 * =================================================================================
 *
 * SISTEMA RESPONSIVE DE 3 BREAKPOINTS:
 *   - LARGE  (width >= 950px): Layout de escritorio completo
 *   - MEDIUM (650 <= width < 950px): KPIs en grilla 2+1, splitGrid lado a lado 55/45
 *   - SMALL  (width < 650px): Todo apilado verticalmente con scroll
 */
@Component
public class DashboardPanel extends JPanel implements Scrollable {

    // ─── Constantes de Breakpoints ───────────────────────────────────────────
    private static final int BP_LARGE  = 950;
    private static final int BP_MEDIUM = 650;

    // ─── Enumerado de estado del layout ──────────────────────────────────────
    private enum LayoutMode { LARGE, MEDIUM, SMALL }

    private final ClientService  clientService;
    private final PaymentService paymentService;
    private final PlanService    planService;

    private DashboardTable     tableRecentPayments;
    private DefaultTableModel  tableModel;

    // [MEJORA JUNIOR] Declaramos los paneles como variables de instancia para poder
    // actualizar sus datos desde cualquier método de la clase.
    private UpcomingExpirationsPanel expirationsPanel;
    private RecentActivityPanel      activityPanel;

    // [MEJORA JUNIOR] Declaramos las tarjetas de métricas como variables de instancia
    // para poder actualizar sus valores numéricos dinámicamente cuando el usuario navegue.
    private MetricCard cardClients;
    private MetricCard cardPlans;
    private MetricCard cardRevenue;

    @Autowired
    public DashboardPanel(ClientService clientService, PaymentService paymentService, PlanService planService) {
        this.clientService  = clientService;
        this.paymentService = paymentService;
        this.planService    = planService;

        initComponentsHandCoded();
        loadDashboardData();
    }

    private void initComponentsHandCoded() {
        setOpaque(true);
        setBackground(Theme.BG_DARK);
        // Row constraints: saludo (pref), KPIs (pref), tabla+lateral (grow), footer (pref)
        setLayout(new MigLayout("wrap 1, ins 20, fill", "[grow]", "[pref][pref][grow][pref]"));

        final MigLayout dashboardLayout = (MigLayout) getLayout();

        // =====================================================================
        // FILA 1: TOPBAR (SALUDO + CÁPSULA DINÁMICA FECHA/CLIMA/PERFIL)
        // =====================================================================
        final MigLayout topBarLayout = new MigLayout("ins 0, fillx", "[grow]push[]");
        final JPanel topBar = new JPanel(topBarLayout);
        topBar.setOpaque(false);

        // ── Bloque de Bienvenida ─────────────────────────────────────────────
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

        // ── Cápsula de Clima, Fecha y Perfil ─────────────────────────────────
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
        // kpiRowLayout — 3 columnas iguales de escritorio; ajustado por el listener
        final MigLayout kpiRowLayout = new MigLayout("ins 0, fill, gapx 16", "[grow][grow][grow]", "[pref]");
        final JPanel kpiRow = new JPanel(kpiRowLayout);
        kpiRow.setOpaque(false);

        // [MEJORA JUNIOR] Inicializamos las tarjetas de métricas en el constructor.
        // Los valores se actualizarán automáticamente cuando carguemos los datos.
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

        // ── 3a. Tabla Registro Reciente de Pagos ─────────────────────────────
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableModel.setColumnIdentifiers(new Object[]{"Cliente", "Membresía", "Fecha Pago", "Monto", "Estado"});
        tableRecentPayments = new DashboardTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tableRecentPayments);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.CARD_BG);

        final JPanel tableCard = new JPanel(new MigLayout("fill, ins 16")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Theme.CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.ARC_CARD, Theme.ARC_CARD);
                g2d.setColor(Theme.BORDER_SLATE);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.ARC_CARD, Theme.ARC_CARD);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        tableCard.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Registro Reciente de Pagos");
        lblTableTitle.setFont(Theme.FONT_SECTION_TITLE);
        lblTableTitle.setForeground(Theme.TEXT_ACTIVE);

        tableCard.add(lblTableTitle, "wrap, gapbottom 12");
        tableCard.add(scrollPane, "grow");
        splitGrid.add(tableCard, "grow");

        // ── 3b. Columna Derecha: Vencimientos + Actividad ─────────────────────
        final JPanel rightColumnPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 16, fill", "[grow]", "[grow][grow]"));
        rightColumnPanel.setOpaque(false);

        // [MEJORA JUNIOR] Instanciamos los paneles y los guardamos en las variables de clase.
        // Así podremos inyectarles los datos reales luego.
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
        final JPanel bottomActionsCard = new JPanel(bottomActionsCardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Theme.CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.ARC_CARD, Theme.ARC_CARD);
                g2d.setColor(Theme.BORDER_SLATE);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.ARC_CARD, Theme.ARC_CARD);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        bottomActionsCard.setOpaque(false);

        JButton btnRegisterSocio = new JButton("Registrar Nuevo Socio",
                new VectorIcon("user-plus", 16, Color.decode("#3b82f6")));
        JButton btnCobrar        = new JButton("Cobrar Membresía",
                new VectorIcon("credit-card", 16, Color.decode("#10b981")));
        JButton btnViewHistory   = new JButton("Ver Historial Completo",
                new VectorIcon("history", 16, Color.decode("#8b5cf6")));

        styleActionButton(btnRegisterSocio);
        styleActionButton(btnCobrar);
        styleActionButton(btnViewHistory);

        btnRegisterSocio.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) mainFrame.selectClientsPanel();
        });
        btnCobrar.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) mainFrame.selectPaymentsPanel();
        });
        btnViewHistory.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) mainFrame.selectHistoryPanel();
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

                    // ── LARGE: Layout completo de escritorio ─────────────────
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

                    // ── MEDIUM: KPIs 2+1, splitGrid 55/45, footer horizontal ──
                    case MEDIUM: {
                        dashboardLayout.setLayoutConstraints("wrap 1, ins 16, fill");
                        dashboardLayout.setColumnConstraints("[grow]");
                        dashboardLayout.setRowConstraints("[pref][pref][pref][grow][pref]");

                        // TopBar: bienvenida + cápsula apiladas
                        topBarLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 8");
                        topBarLayout.setColumnConstraints("[grow]");
                        topBar.removeAll();
                        topBar.add(welcomePanel,   "growx");
                        topBar.add(weatherCapsule, "alignx right");

                        // KPIs: fila 1 = clientes + planes, fila 2 = recaudación centrada
                        kpiRowLayout.setLayoutConstraints("ins 0, fillx, gapx 12, gapy 12");
                        kpiRowLayout.setColumnConstraints("[grow][grow]");
                        kpiRow.removeAll();
                        kpiRow.add(cardClients, "grow");
                        kpiRow.add(cardPlans,   "grow");
                        kpiRow.add(cardRevenue, "grow, span 2");

                        // SplitGrid sigue lado a lado pero proporciones 55/45
                        splitGridLayout.setLayoutConstraints("ins 0, fill");
                        splitGridLayout.setColumnConstraints("[55%, grow]12[45%, grow]");
                        splitGridLayout.setRowConstraints("[grow]");
                        splitGrid.removeAll();
                        splitGrid.add(tableCard,        "grow");
                        splitGrid.add(rightColumnPanel, "grow");

                        // Footer 2 columnas + 1 centrada
                        bottomActionsCardLayout.setLayoutConstraints("ins 12 16 12 16, fillx, aligny center, gapx 8");
                        bottomActionsCardLayout.setColumnConstraints("[grow][grow][grow]");

                        add(topBar,            "growx, gapbottom 12");
                        add(kpiRow,            "growx, gapbottom 12");
                        add(splitGrid,         "grow, gapbottom 10");
                        add(bottomActionsCard, "growx");
                        break;
                    }

                    // ── SMALL: Todo apilado verticalmente ────────────────────
                    case SMALL: {
                        dashboardLayout.setLayoutConstraints("wrap 1, ins 12, fillx, gapy 12");
                        dashboardLayout.setColumnConstraints("[grow, fill]");
                        dashboardLayout.setRowConstraints("");

                        // TopBar apilado
                        topBarLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 6");
                        topBarLayout.setColumnConstraints("[grow]");
                        topBar.removeAll();
                        topBar.add(welcomePanel,   "growx");
                        topBar.add(weatherCapsule, "alignx right");

                        // KPIs: apiladas verticalmente
                        kpiRowLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 10");
                        kpiRowLayout.setColumnConstraints("[grow, fill]");
                        kpiRow.removeAll();
                        kpiRow.add(cardClients, "growx");
                        kpiRow.add(cardPlans,   "growx");
                        kpiRow.add(cardRevenue, "growx");

                        // SplitGrid apilado
                        splitGridLayout.setLayoutConstraints("wrap 1, ins 0, fillx, gapy 12");
                        splitGridLayout.setColumnConstraints("[grow, fill]");
                        splitGridLayout.setRowConstraints("");
                        splitGrid.removeAll();
                        splitGrid.add(tableCard,        "growx");
                        splitGrid.add(rightColumnPanel, "growx");

                        // Footer apilado: botones uno debajo del otro
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

    // =========================================================================
    // IMPLEMENTACIÓN DE Scrollable
    // =========================================================================
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
        return true; // Sin scroll horizontal
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // Expandir si el contenido cabe; hacer scroll si no
        if (getParent() instanceof JViewport) {
            return getPreferredSize().height <= ((JViewport) getParent()).getHeight();
        }
        return false;
    }

    // =========================================================================
    // ESTILO DE BOTONES DE ACCIÓN
    // =========================================================================
    private void styleActionButton(JButton button) {
        button.setIconTextGap(10);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "toolBarButton");
        button.putClientProperty("FlatLaf.style",
                "arc: 12; " +
                "margin: 8 16 8 16; " +
                "foreground: #f8fafc; " +
                "hoverBackground: #1e293b; " +
                "hoverForeground: #3b82f6;");
    }

    // [MEJORA JUNIOR] Se eliminaron SHORT_DATE_FORMATTER y formatMoneyValue
    // delegando la conversión y formato visual a la nueva clase compartida FormatterUtils.
    // =========================================================================
    // CARGA DE DATOS
    // =========================================================================
    private void loadDashboardData() {
        // [MEJORA JUNIOR] Cada vez que cargamos los datos del Dashboard,
        // recalculamos las métricas principales de la base de datos y actualizamos los componentes.
        long activeClients = clientService.findByIsActive(true).size();
        if (cardClients != null) {
            cardClients.setValue(String.valueOf(activeClients));
        }

        long activePlans = planService.findByIsActiveDTO(true).size();
        if (cardPlans != null) {
            cardPlans.setValue(String.valueOf(activePlans));
        }

        List<Payment> allPayments = paymentService.findAll();
        BigDecimal totalRevenue = allPayments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.CONFIRMADO)
                .map(Payment::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (cardRevenue != null) {
            cardRevenue.setValue(FormatterUtils.formatCurrency(totalRevenue));
        }

        List<PaymentDTO> payments = paymentService.getAllPaymentsDTO();

        List<PaymentDTO> recent = payments.stream()
                .sorted((p1, p2) -> {
                    Long id1 = p1.getIdPayment();
                    Long id2 = p2.getIdPayment();
                    if (id1 == null && id2 == null) return 0;
                    if (id1 == null) return 1;
                    if (id2 == null) return -1;
                    return Long.compare(id2, id1);
                })
                .limit(4)
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (PaymentDTO dto : recent) {
            tableModel.addRow(new Object[]{
                    dto.getNameClient(),
                    dto.getNamePlan(),
                    FormatterUtils.formatDate(dto.getPaymentDate()),
                    FormatterUtils.formatCurrency(dto.getFinalAmount()),
                    dto.getPaymentStatus()
            });
        }

        // [MEJORA JUNIOR] Filtramos pagos PENDIENTES o VENCIDOS, los ordenamos por fecha
        // y tomamos los primeros 3 para el panel de "Próximos Vencimientos".
        List<Payment> pending = paymentService.findAll().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDIENTE || p.getPaymentStatus() == PaymentStatus.VENCIDO)
                .sorted(java.util.Comparator.comparing(Payment::getPeriod))
                .limit(3)
                .collect(Collectors.toList());
        expirationsPanel.updateData(pending);

        // [MEJORA JUNIOR] Ordenamos todos los pagos del más reciente al más antiguo
        // usando el ID y tomamos los últimos 4 para el panel de "Actividad Reciente".
        List<Payment> recentPays = paymentService.findAll().stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .limit(4)
                .collect(Collectors.toList());
        activityPanel.updateData(recentPays);
    }

    // [MEJORA JUNIOR] Método público para refrescar todo el Dashboard desde componentes externos (como MainFrame).
    public void refreshData() {
        loadDashboardData();
    }

    // =========================================================================
    // PANEL: PRÓXIMOS VENCIMIENTOS
    // =========================================================================
    // [MEJORA JUNIOR] Le quitamos "static" para que pueda acceder a los métodos
    // e instancias de la clase principal, y creamos un método para actualizar los datos.
    private class UpcomingExpirationsPanel extends JPanel {

        private JPanel listPanel;

        public UpcomingExpirationsPanel() {
            setOpaque(false);
            initComponents();
        }

        private void initComponents() {
            setLayout(new MigLayout("wrap 1, ins 18 20 18 20, fill", "[grow]", "[]10[grow]"));

            JPanel headerPanel = new JPanel(new MigLayout("ins 0, fillx", "[grow]push[]"));
            headerPanel.setOpaque(false);

            JLabel lblHeader = new JLabel("Próximos Vencimientos");
            lblHeader.setFont(Theme.FONT_SECTION_TITLE);
            lblHeader.setForeground(Theme.TEXT_ACTIVE);
            headerPanel.add(lblHeader, "left");

            JLabel lblLink = new JLabel("Ver todos");
            lblLink.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblLink.setForeground(Theme.TEXT_INACTIVE);
            lblLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
            headerPanel.add(lblLink, "right");

            add(headerPanel, "cell 0 0, growx");

            listPanel = new JPanel(new MigLayout("wrap 1, ins 0, gapy 8, fillx"));
            listPanel.setOpaque(false);
            add(listPanel, "cell 0 1, grow");
        }

        // [MEJORA JUNIOR] Método que limpia la lista y construye dinámicamente los items.
        public void updateData(List<Payment> pendingPayments) {
            listPanel.removeAll();
            if (pendingPayments.isEmpty()) {
                JLabel emptyLabel = new JLabel("No hay pagos pendientes");
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                emptyLabel.setForeground(Theme.TEXT_INACTIVE);
                listPanel.add(emptyLabel, "align center");
            } else {
                for (Payment p : pendingPayments) {
                    com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client c = p.getClient();
                    String name = c.getName() + " " + c.getLastName();
                    String plan = c.getCurrentPlan() != null ? c.getCurrentPlan().getNamePlan() : "Sin Plan";
                    
                    String initials = "";
                    if (!name.trim().isEmpty()) {
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
                    String alertText = "Vence: " + p.getPeriod().format(formatter);
                    Color alertColor = Color.decode("#f59e0b");
                    Color badgeBg = new Color(245, 158, 11, 30);
                    
                    if (p.getPaymentStatus() == PaymentStatus.VENCIDO) {
                        alertText = "VENCIDO";
                        alertColor = Color.decode("#ef4444");
                        badgeBg = new Color(239, 68, 68, 30);
                    }
                    
                    listPanel.add(new ExpirationItem(name, plan, alertText, initials, badgeBg, alertColor), "growx");
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Theme.CARD_BG);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.ARC_CARD, Theme.ARC_CARD);
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.ARC_CARD, Theme.ARC_CARD);
            g2d.dispose();
        }
    }

    // =========================================================================
    // ITEM: FILA DE VENCIMIENTO
    // =========================================================================
    private static class ExpirationItem extends JPanel {

        public ExpirationItem(String name, String planName, String alertText,
                              String initials, Color badgeBgColor, Color alertTextColor) {
            setOpaque(false);
            setLayout(new MigLayout("ins 8 10 8 10, fill", "[32px!]10[grow][pref]", "[grow]"));

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
                    int tx = (getWidth()  - fm.stringWidth(initials)) / 2;
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

            add(badge,     "cell 0 0, aligny center");
            add(textPanel, "cell 1 0, aligny center");
            add(lblAlert,  "cell 2 0, aligny center");
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

    // =========================================================================
    // PANEL: ACTIVIDAD RECIENTE
    // =========================================================================
    // [MEJORA JUNIOR] Igual que el panel anterior, lo hacemos no estático para
    // poder poblarlo dinámicamente con información real desde la base de datos.
    private class RecentActivityPanel extends JPanel {

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

        // [MEJORA JUNIOR] Actualizamos la actividad reciente con los últimos pagos reales.
        public void updateData(List<Payment> recentPayments) {
            listPanel.removeAll();
            if (recentPayments.isEmpty()) {
                JLabel emptyLabel = new JLabel("Sin actividad reciente");
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                emptyLabel.setForeground(Theme.TEXT_INACTIVE);
                listPanel.add(emptyLabel, "align center");
            } else {
                for (Payment p : recentPayments) {
                    com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client c = p.getClient();
                    String name = c.getName() + " " + c.getLastName();
                    String text = name + " pagó " + (c.getCurrentPlan() != null ? c.getCurrentPlan().getNamePlan() : "membresía");
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String timeText = "Fecha: " + p.getPaymentDate().format(formatter);
                    
                    Color iconColor = Color.decode("#10b981");
                    Color badgeBg = new Color(16, 185, 129, 30);
                    String iconType = "check";
                    
                    if (p.getPaymentStatus() == PaymentStatus.PENDIENTE) {
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
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Theme.CARD_BG);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.ARC_CARD, Theme.ARC_CARD);
            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.ARC_CARD, Theme.ARC_CARD);
            g2d.dispose();
        }
    }

    // =========================================================================
    // ITEM: FILA DE ACTIVIDAD
    // =========================================================================
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
