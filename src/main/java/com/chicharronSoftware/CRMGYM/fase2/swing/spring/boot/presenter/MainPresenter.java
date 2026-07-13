package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.MainFrame;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.SidebarPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.StatusBarPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard.DashboardPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.ClientPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.HistoricalPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.PaymentPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.PlansPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Profile("!test")
public class MainPresenter {

    private final MainFrame mainFrame;
    private final SidebarPanel sidebarPanel;
    private final StatusBarPanel statusBarPanel;
    private final DashboardPanel dashboardPanel;
    private final PlansPanel plansPanel;
    private final ClientPanel clientPanel;
    private final PaymentPanel paymentPanel;
    private final HistoricalPanel historicalPanel;
    private final ClientService clientService;
    private final DashboardPresenter dashboardPresenter;

    @Autowired
    public MainPresenter(MainFrame mainFrame, SidebarPanel sidebarPanel, StatusBarPanel statusBarPanel,
            DashboardPanel dashboardPanel, PlansPanel plansPanel, ClientPanel clientPanel,
            PaymentPanel paymentPanel, HistoricalPanel historicalPanel, ClientService clientService,
            DashboardPresenter dashboardPresenter) {
        this.mainFrame = mainFrame;
        this.sidebarPanel = sidebarPanel;
        this.statusBarPanel = statusBarPanel;
        this.dashboardPanel = dashboardPanel;
        this.plansPanel = plansPanel;
        this.clientPanel = clientPanel;
        this.paymentPanel = paymentPanel;
        this.historicalPanel = historicalPanel;
        this.clientService = clientService;
        this.dashboardPresenter = dashboardPresenter;
    }

    @PostConstruct
    public void init() {
        // Wire mainFrame's helper methods to presenter's router
        mainFrame.setNavigationListener(this::navigateTo);

        // Bind clicks from sidebar
        sidebarPanel.addNavigationListener("Inicio", e -> navigateTo("Inicio"));
        sidebarPanel.addNavigationListener("Socios", e -> navigateTo("Socios"));
        sidebarPanel.addNavigationListener("Planes", e -> navigateTo("Planes"));
        sidebarPanel.addNavigationListener("Pagos", e -> navigateTo("Pagos"));
        sidebarPanel.addNavigationListener("Historial", e -> navigateTo("Historial"));

        // Check DB connection on start asynchronously to keep EDT free
        checkDatabaseConnectionAsync();

        // Default screen
        navigateTo("Inicio");
    }

    private void navigateTo(String panelName) {
        sidebarPanel.setActiveButtonByText(panelName);
        switch (panelName) {
            case "Inicio":
                dashboardPresenter.refreshData();
                mainFrame.showDashboard(dashboardPanel);
                break;
            case "Socios":
                mainFrame.showPanel(clientPanel);
                break;
            case "Planes":
                mainFrame.showPanel(plansPanel);
                break;
            case "Pagos":
                mainFrame.showPanel(paymentPanel);
                break;
            case "Historial":
                mainFrame.showPanel(historicalPanel);
                break;
        }
    }

    private void checkDatabaseConnectionAsync() {
        AsyncDataLoader.loadData(
                () -> {
                    // Call a lightweight DB check
                    clientService.findByIsActiveDTO(true);
                    return true;
                },
                new AsyncDataLoader.DataLoadCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        statusBarPanel.setConnectionStatus(true, "crmgym_fase2");
                    }

                    @Override
                    public void onError(Exception ex) {
                        statusBarPanel.setConnectionStatus(false, "");
                    }
                });
    }
}
