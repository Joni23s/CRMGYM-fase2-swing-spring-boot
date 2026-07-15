package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.facade;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.DashboardDataDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardFacade {

    private final ClientService clientService;
    private final PaymentService paymentService;
    private final PlanService planService;

    public DashboardDataDTO getDashboardData() {
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
        List<PaymentDTO> pending = allPayments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDIENTE || p.getPaymentStatus() == PaymentStatus.VENCIDO)
                .sorted(java.util.Comparator.comparing(Payment::getPeriod))
                .limit(3)
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());

        // Recent payments list for Activity panel (sorted by ID descending, limit 4)
        List<PaymentDTO> recentPays = allPayments.stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .limit(4)
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());

        return new DashboardDataDTO(activeClients, activePlans, totalRevenue, recent, pending, recentPays);
    }
}
