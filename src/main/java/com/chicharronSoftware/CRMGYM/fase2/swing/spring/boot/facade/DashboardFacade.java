package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.facade;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.DashboardDataDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.ClientRepository;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PaymentRepository;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [MEJORA JUNIOR] Fachada para la recolección de métricas del Dashboard.
 * Consolida consultas de agregación directas en la base de datos (COUNT, SUM, TOP-N)
 * evitando traer listas enteras a la memoria RAM, lo que garantiza máxima velocidad y escalabilidad.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardFacade {

    private final ClientRepository clientRepository;
    private final PaymentRepository paymentRepository;
    private final PlanRepository planRepository;

    public DashboardDataDTO getDashboardData() {
        // [MEJORA JUNIOR] Consultas de agregación numéricas directas en BD
        long activeClients = clientRepository.countByIsActive(true);
        long activePlans = planRepository.countByIsActive(true);
        BigDecimal totalRevenue = paymentRepository.sumTotalConfirmedRevenue();

        // Lista de pagos recientes (últimos 4 por ID descendente)
        List<PaymentDTO> recent = paymentRepository.findTop4ByOrderByIdDesc()
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());

        // Pagos pendientes o vencidos (primeros 3 ordenados por período)
        List<PaymentDTO> pending = paymentRepository.findTop3ByPaymentStatusInOrderByPeriodAsc(
                        Arrays.asList(PaymentStatus.PENDIENTE, PaymentStatus.VENCIDO))
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());

        // Lista de actividad reciente
        List<PaymentDTO> recentPays = recent;

        // Arqueo de caja del día por medio de pago
        java.time.LocalDate today = java.time.LocalDate.now();
        BigDecimal cashToday = paymentRepository.sumTodayRevenueByMethod(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.EFECTIVO, today);
        BigDecimal transferToday = paymentRepository.sumTodayRevenueByMethod(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.TRANSFERENCIA, today);
        BigDecimal debitToday = paymentRepository.sumTodayRevenueByMethod(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.TARJETA_DEBITO, today);

        return new DashboardDataDTO(activeClients, activePlans, totalRevenue, recent, pending, recentPays, cashToday, transferToday, debitToday);
    }
}
