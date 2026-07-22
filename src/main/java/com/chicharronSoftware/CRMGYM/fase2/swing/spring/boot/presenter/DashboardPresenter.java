package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.DashboardDataDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.facade.DashboardFacade;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.FormatterUtils;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard.DashboardPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * [MEJORA JUNIOR] Presentador para la pantalla de Inicio (Dashboard).
 * Coordina la recolección asíncrona de datos desde la fachada DashboardFacade
 * y actualiza los componentes gráficos del POS sin congelar la UI.
 */
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
                    // Actualizar Métricas KPI simplificadas
                    view.updateMetrics(
                        String.valueOf(data.getActiveClients()),
                        FormatterUtils.formatCurrency(data.getTotalRevenue())
                    );
                    
                    // Actualizar Registros de Pagos y Vencimientos
                    view.updateRecentPaymentsTable(data.getRecentPayments());
                    view.updateUpcomingExpirations(data.getPendingPayments());

                    // Actualizar Arqueo de Caja del Día
                    BigDecimal cash = data.getTodayCashTotal() != null ? data.getTodayCashTotal() : BigDecimal.ZERO;
                    BigDecimal transfer = data.getTodayTransferTotal() != null ? data.getTodayTransferTotal() : BigDecimal.ZERO;
                    BigDecimal debit = data.getTodayDebitTotal() != null ? data.getTodayDebitTotal() : BigDecimal.ZERO;
                    BigDecimal dayTotal = cash.add(transfer).add(debit);

                    view.updateCashDeskSummary(
                        FormatterUtils.formatCurrency(dayTotal),
                        FormatterUtils.formatCurrency(cash),
                        FormatterUtils.formatCurrency(transfer),
                        FormatterUtils.formatCurrency(debit)
                    );
                }

                @Override
                public void onError(Exception ex) {
                    // Si falla la recolección asíncrona, se registra silenciosamente para mantener la fluidez del POS
                }
            }
        );
    }
}
