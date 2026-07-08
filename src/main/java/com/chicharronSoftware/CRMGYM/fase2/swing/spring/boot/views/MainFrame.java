package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.HistoricalPlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations.ClientValidation;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations.PlanValidation;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard.DashboardPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.ClientPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.HistoricalPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.PaymentPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.PlansPanel;

import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
// [MEJORA JUNIOR] Se limpiaron imports que no se utilizaban (como BigDecimal y NumberFormat) para mantener el archivo más limpio.

/**
 * =================================================================================
 * GUÍA DE ARQUITECTURA: PATRÓN APLICACIÓN DE CARCASA (SHELL ARCHITECTURE PATTERN)
 * =================================================================================
 * 
 * 1. EL PATRÓN SHELL (APLICACIÓN CARCASA):
 *    - La Shell (Carcasa) es una ventana de nivel superior que define el marco estructural
 *      global del software (como el menú de navegación lateral y la barra de estado inferior)
 *      pero delega por completo la representación visual del contenido dinámico al centro.
 *    - MainFrame actúa como esta carcasa. No sabe cómo pintar gráficos, ni KPIs, ni tablas;
 *      únicamente proporciona el área central (`centerArea`) para realizar intercambios de
 *      paneles según los eventos de la barra de navegación lateral.
 * 
 * 2. INTEGRACIÓN CON EL CONTENEDOR IOC DE SPRING BOOT:
 *    - @Component: Permite a Spring Boot gestionar MainFrame como un Singleton en su contexto de inicio.
 *    - El constructor recibe el bean `DashboardPanel` (que a su vez tiene inyectados sus servicios).
 *      Esto crea una cadena limpia de inyección por constructor de dependencias controladas, evitando
 *      inicializaciones manuales redundantes en el hilo visual.
 * 
 * 3. IMPORTANCIA DE revalidate() Y repaint() EN EL EDT:
 *    - Cuando llamamos a `centerArea.removeAll()`, los componentes son removidos de la jerarquía de Swing.
 *      Sin embargo, el Layout Manager (en este caso MigLayout) no detecta automáticamente la alteración
 *      del árbol hasta que se le notifique explícitamente.
 *    - `revalidate()`: Le dice a Swing que recalcule de manera síncrona el posicionamiento y dimensiones
 *      de los componentes hijos del panel según las nuevas restricciones.
 *    - `repaint()`: Encola la petición de dibujo sobre el Event Dispatch Thread (EDT) para renderizar
 *      nuevamente los píxeles en pantalla de forma limpia sin residuos visuales de paneles anteriores.
 * =================================================================================
 */
@Component
public class MainFrame extends JFrame {

    private final ClientService clientService;
    private final PlanService planService;
    private final HistoricalPlanService historicalPlanService;
    private final PaymentService paymentService;
    private final PlanValidation planValidation;
    private final ClientValidation clientValidation;
    private final ButtonFactory buttonFactory; // [MEJORA JUNIOR] Fábrica inyectada para pasar a paneles

    // Inyección del panel modular de Dashboard centralizado por Spring Boot
    private final DashboardPanel dashboardPanel;

    private SidebarPanel sidebarPanel;
    private JPanel centerArea;

    @Autowired
    public MainFrame(DashboardPanel dashboardPanel, ClientService clientService, PlanService planService,
            HistoricalPlanService historicalPlanService, PaymentService paymentService,
            PlanValidation planValidation, ClientValidation clientValidation, ButtonFactory buttonFactory) {
        this.dashboardPanel = dashboardPanel;
        this.clientService = clientService;
        this.planService = planService;
        this.historicalPlanService = historicalPlanService;
        this.paymentService = paymentService;
        this.planValidation = planValidation;
        this.clientValidation = clientValidation;
        this.buttonFactory = buttonFactory;

        initComponents();
    }

    private void initComponents() {
        setTitle("CRMGYM - Sistema de Gestión Avanzada para Gimnasios [v2.0.0]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Arrancar maximizada por defecto

        // --- Configuración del Panel de Contenido Principal (Shell Frame) ---
        // Columna 1 fija de 220px, Columna 2 expansiva.
        // Fila 1 expansiva, Fila 2 fija de 30px.
        JPanel rootPanel = new JPanel(
                new MigLayout("fill, ins 0, gap 0", "[220px!]0[grow, fill]", "[grow, fill]0[30px!]"));
        rootPanel.setBackground(Color.decode("#0b0f19")); // Fondo general ultra oscuro
        setContentPane(rootPanel);

        // --- 1. Menú Lateral (SidebarPanel) ---
        sidebarPanel = new SidebarPanel();
        rootPanel.add(sidebarPanel, "cell 0 0, grow");

        // --- 2. Área Central de Intercambio (Swapping de componentes) ---
        centerArea = new JPanel(new MigLayout("fill, ins 0"));
        centerArea.setOpaque(false);
        rootPanel.add(centerArea, "cell 1 0, grow");

        // --- 3. Barra de Estado Inferior ---
        StatusBarPanel statusBarPanel = new StatusBarPanel();
        rootPanel.add(statusBarPanel, "cell 0 1 2 1, grow");

        // --- Mapeo de Eventos de Navegación del Sidebar ---
        sidebarPanel.addNavigationListener("Inicio", e -> showDashboardHome());
        sidebarPanel.addNavigationListener("Socios",
                e -> showPanel(new ClientPanel(clientService, planService, clientValidation, buttonFactory)));
        sidebarPanel.addNavigationListener("Planes",
                e -> showPanel(new PlansPanel(planService, planValidation, clientService, buttonFactory)));
        sidebarPanel.addNavigationListener("Pagos", e -> showPanel(new PaymentPanel(paymentService, clientService, buttonFactory)));
        sidebarPanel.addNavigationListener("Historial",
                e -> showPanel(new HistoricalPanel(clientService, planService, historicalPlanService, buttonFactory)));

        // Mostrar la pantalla de Inicio por defecto al iniciar la aplicación
        showDashboardHome();
    }

    /**
     * Intercambia el contenedor para presentar la pantalla modular del Dashboard.
     * Desacopla todo el pintado directo de KPIs y Gráficos hacia DashboardPanel.
     */
    private void showDashboardHome() {
        // [MEJORA JUNIOR] Invocamos al método de refresco para que recalcule
        // todas las métricas de socios y caja antes de pintar la pantalla principal.
        dashboardPanel.refreshData();

        // 1. Limpiar el contenedor central donde se montan las vistas
        centerArea.removeAll(); 

        // 2. Envolver el panel en un JScrollPane para permitir scroll vertical
        //    cuando la ventana es más pequeña que el contenido preferido.
        //    DashboardPanel implementa Scrollable: llena el ancho, scroll solo vertical.
        JScrollPane scrollWrapper = new JScrollPane(dashboardPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollWrapper.setBorder(BorderFactory.createEmptyBorder());
        scrollWrapper.getViewport().setBackground(Color.decode("#0b0f19"));
        scrollWrapper.getVerticalScrollBar().setUnitIncrement(16);

        centerArea.add(scrollWrapper, "grow"); 

        // 3. Forzar a Swing y al EDT a recalcular la geometría del árbol de componentes
        centerArea.revalidate(); 
        centerArea.repaint();
    }

    /**
     * Intercambia el panel en el área central para las vistas del menú secundario.
     */
    public void showPanel(JPanel panel) {
        centerArea.removeAll();
        centerArea.add(panel, "grow");
        centerArea.revalidate();
        centerArea.repaint();
    }

    // Métodos auxiliares para la navegación interactiva desde otros paneles
    public void selectClientsPanel() {
        sidebarPanel.setActiveButtonByText("Socios");
        showPanel(new ClientPanel(clientService, planService, clientValidation, buttonFactory));
    }

    public void selectHistoryPanel() {
        sidebarPanel.setActiveButtonByText("Historial");
        showPanel(new HistoricalPanel(clientService, planService, historicalPlanService, buttonFactory));
    }

    public void selectPaymentsPanel() {
        sidebarPanel.setActiveButtonByText("Pagos");
        showPanel(new PaymentPanel(paymentService, clientService, buttonFactory));
    }

    // [MEJORA JUNIOR] Se eliminó el método 'formatCurrency' ya que no se estaba utilizando en esta clase (código muerto).
}
