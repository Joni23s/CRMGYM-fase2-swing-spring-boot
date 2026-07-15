package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.DashboardDataDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.facade.DashboardFacade;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.FormatterUtils;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard.DashboardPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DashboardPresenter {

    private final DashboardPanel view;
    private final DashboardFacade dashboardFacade;

    @Autowired
    public DashboardPresenter(DashboardPanel view, DashboardFacade dashboardFacade) {
        this.view = view;
        this.dashboardFacade = dashboardFacade;
    }

    public void refreshData() {
        // Ejecutar obtención de datos en hilos de background de forma asíncrona
        AsyncDataLoader.loadData(
            () -> dashboardFacade.getDashboardData(),
            new AsyncDataLoader.DataLoadCallback<DashboardDataDTO>() {
                @Override
                public void onSuccess(DashboardDataDTO data) {
                    view.updateMetrics(
                        String.valueOf(data.getActiveClients()),
                        String.valueOf(data.getActivePlans()),
                        FormatterUtils.formatCurrency(data.getTotalRevenue())
                    );
                    view.updateRecentPaymentsTable(data.getRecentPayments());
                    view.updateUpcomingExpirations(data.getPendingPayments());
                    view.updateRecentActivity(data.getRecentPays());
                }

                @Override
                public void onError(Exception ex) {
                    // Si falla el dashboard, lo loggeamos o simplemente no lo actualizamos
                }
            }
        );
    }
}
