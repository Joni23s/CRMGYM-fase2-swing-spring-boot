package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.ClientRepository;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PaymentRepository;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [MEJORA JUNIOR] Servicio para administrar los pagos de las membresías de los socios.
 * Permite registrar cobros, buscar transacciones anteriores y verificar vencimientos de cuotas.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final PlanRepository planRepository;

    // --- 🔹 CRUD básico ---
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    /**
     * [MEJORA JUNIOR] Registra un pago en la base de datos.
     * Si no se especificó un plan de forma explícita en el pago, se congela el 'currentPlan' actual
     * del socio como la versión histórica cobrada en esta transacción.
     */
    public Payment save(Payment payment) {
        if (payment.getPlan() == null && payment.getClient() != null) {
            payment.setPlan(payment.getClient().getCurrentPlan());
        }
        return paymentRepository.save(payment);
    }

    // --- Consultas comunes ---
    public List<Payment> findByClientDocumentId(int documentId) {
        return paymentRepository.findByClient_DocumentId(documentId);
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    // --- DTO Helpers ---
    public List<PaymentDTO> getAllPaymentsDTO() {
        // [MEJORA JUNIOR] Retornamos la lista mapeada a DTO para evitar exponer
        // entidades persistidas a la capa visual de Swing.
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    // --- Lógica de negocio ---
    public List<Payment> findAllByClientId(Integer clientId) {
        return paymentRepository.findByClient_DocumentIdOrderByPaymentDateDesc(clientId);
    }

    public Optional<Payment> findLastPaymentByClientId(Integer clientId) {
        return paymentRepository.findTopByClient_DocumentIdOrderByPaymentDateDesc(clientId);
    }

    /**
     * [MEJORA JUNIOR] Guarda un pago a partir de un DTO, desacoplando la UI de Swing de JPA.
     */
    public PaymentDTO saveDTO(PaymentDTO dto) {
        Client client = null;
        if (dto.getDocumentId() != null) {
            client = clientRepository.findByDocumentId(dto.getDocumentId()).orElse(null);
        }
        Plan plan = null;
        if (dto.getNamePlan() != null && !dto.getNamePlan().isBlank()) {
            plan = planRepository.findByNamePlanIgnoreCase(dto.getNamePlan()).orElse(null);
        }
        if (plan == null && client != null) {
            plan = client.getCurrentPlan();
        }

        final Client finalClient = client;
        final Plan finalPlan = plan;

        com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment payment;
        if (dto.getIdPayment() != null) {
            payment = paymentRepository.findById(dto.getIdPayment()).orElseGet(() -> PaymentMapper.toEntity(dto, finalClient, finalPlan));
            payment.setPeriod(dto.getPeriod());
            payment.setPaymentDate(dto.getPaymentDate());
            payment.setBaseAmount(dto.getBaseAmount());
            payment.setDiscountApplied(dto.getDiscountApplied());
            payment.setFinalAmount(dto.getFinalAmount());
            if (dto.getPaymentMethod() != null && !dto.getPaymentMethod().isBlank()) {
                payment.setPaymentMethod(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.valueOf(dto.getPaymentMethod()));
            }
            if (dto.getPaymentStatus() != null && !dto.getPaymentStatus().isBlank()) {
                payment.setPaymentStatus(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus.valueOf(dto.getPaymentStatus()));
            }
            if (finalClient != null) payment.setClient(finalClient);
            if (finalPlan != null) payment.setPlan(finalPlan);
        } else {
            payment = PaymentMapper.toEntity(dto, finalClient, finalPlan);
        }

        com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment saved = save(payment);
        return PaymentMapper.toDTO(saved);
    }

    public List<PaymentDTO> findByStatusDTO(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status)
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findByClientDocumentIdDTO(Integer documentId) {
        return paymentRepository.findByClient_DocumentIdOrderByPaymentDateDesc(documentId)
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PaymentDTO> findLastPaymentByClientIdDTO(Integer clientId) {
        return paymentRepository.findTopByClient_DocumentIdOrderByPaymentDateDesc(clientId)
                .map(PaymentMapper::toDTO);
    }

    /**
     * [MEJORA JUNIOR] Proceso programado para marcar pagos vencidos.
     * Marca un pago como vencido si ya pasó la fecha límite de pago de su período.
     * 
     * @param today Fecha actual de referencia para evaluar los vencimientos.
     */
    public void markOverduePayments(LocalDate today) {
        // [MEJORA JUNIOR] En lugar de obtener todos los pagos pendientes y recorrerlos
        // en un bucle for() (lo que genera una consulta SELECT y múltiples UPDATE por cada pago),
        // llamamos a la query masiva que actualiza todo directamente en la base de
        // datos de una vez (Bulk Update). Esto reduce el tráfico de red e I/O en BD.
        paymentRepository.markOverduePaymentsBulk(today);
    }
}
