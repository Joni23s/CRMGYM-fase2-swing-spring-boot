package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.FormatterUtils;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.dashboard.DashboardPanel;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DashboardPresenter {

    private final DashboardPanel view;
    private final ClientService clientService;
    private final PaymentService paymentService;
    private final PlanService planService;

    @Autowired
    public DashboardPresenter(DashboardPanel view, ClientService clientService,
                              PaymentService paymentService, PlanService planService) {
        this.view = view;
        this.clientService = clientService;
        this.paymentService = paymentService;
        this.planService = planService;
    }

    public void refreshData() {
        // Ejecutar obtención de datos en hilos de background de forma asíncrona
        AsyncDataLoader.loadData(
            () -> {
                // KPIs
                long activeClients = clientService.findByIsActive(true).size();
                long activePlans = planService.findByIsActiveDTO(true).size();

                List<Payment> allPayments = paymentService.findAll();
                BigDecimal totalRevenue = allPayments.stream()
                        .filter(p -> p.getPaymentStatus() == PaymentStatus.CONFIRMADO)
                        .map(Payment::getFinalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Recent payments list (sorted and limited to 4)
                List<PaymentDTO> paymentsDTO = paymentService.getAllPaymentsDTO();
                List<PaymentDTO> recent = paymentsDTO.stream()
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

                // Pending/expired payments for Upcoming Expirations
                List<Payment> pending = allPayments.stream()
                        .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDIENTE || p.getPaymentStatus() == PaymentStatus.VENCIDO)
                        .sorted(java.util.Comparator.comparing(Payment::getPeriod))
                        .limit(3)
                        .collect(Collectors.toList());

                // Recent payments list for Activity panel (sorted by ID descending, limit 4)
                List<Payment> recentPays = allPayments.stream()
                        .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                        .limit(4)
                        .collect(Collectors.toList());

                return new DashboardData(activeClients, activePlans, totalRevenue, recent, pending, recentPays);
            },
            new AsyncDataLoader.DataLoadCallback<DashboardData>() {
                @Override
                public void onSuccess(DashboardData data) {
                    view.updateMetrics(
                        String.valueOf(data.activeClients),
                        String.valueOf(data.activePlans),
                        FormatterUtils.formatCurrency(data.totalRevenue)
                    );
                    view.updateRecentPaymentsTable(data.recentPayments);
                    view.updateUpcomingExpirations(data.pendingPayments);
                    view.updateRecentActivity(data.recentPays);
                }

                @Override
                public void onError(Exception ex) {
                    // Si falla el dashboard, lo loggeamos o simplemente no lo actualizamos
                    // (ya se encarga el GlobalExceptionHandler de alertar de ser necesario)
                }
            }
        );
    }

    private static class DashboardData {
        final long activeClients;
        final long activePlans;
        final BigDecimal totalRevenue;
        final List<PaymentDTO> recentPayments;
        final List<Payment> pendingPayments;
        final List<Payment> recentPays;

        DashboardData(long activeClients, long activePlans, BigDecimal totalRevenue,
                      List<PaymentDTO> recentPayments, List<Payment> pendingPayments, List<Payment> recentPays) {
            this.activeClients = activeClients;
            this.activePlans = activePlans;
            this.totalRevenue = totalRevenue;
            this.recentPayments = recentPayments;
            this.pendingPayments = pendingPayments;
            this.recentPays = recentPays;
        }
    }
}
