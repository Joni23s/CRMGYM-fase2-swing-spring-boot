package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.ClientMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.ClientRepository clientRepository;

    // --- 🔹 CRUD básico ---
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    /**
     * [MEJORA JUNIOR] Sobrecarga de guardado que acepta un DTO de pago.
     * Resuelve el cliente asociado mediante su DNI y convierte el DTO a entidad de dominio.
     */
    public void save(PaymentDTO paymentDTO) {
        Client client = clientRepository.findById(paymentDTO.getDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un cliente con DNI " + paymentDTO.getDocumentId()));

        Payment payment;
        if (paymentDTO.getIdPayment() != null) {
            payment = paymentRepository.findById(paymentDTO.getIdPayment())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el pago a modificar."));
            payment.setPeriod(paymentDTO.getPeriod());
            payment.setPaymentDate(paymentDTO.getPaymentDate());
            payment.setBaseAmount(paymentDTO.getBaseAmount());
            payment.setDiscountApplied(paymentDTO.getDiscountApplied());
            payment.setFinalAmount(paymentDTO.getFinalAmount());
            payment.setPaymentMethod(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.valueOf(paymentDTO.getPaymentMethod()));
            payment.setPaymentStatus(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus.valueOf(paymentDTO.getPaymentStatus()));
            payment.setClient(client);
        } else {
            payment = Payment.builder()
                    .period(paymentDTO.getPeriod())
                    .paymentDate(paymentDTO.getPaymentDate())
                    .baseAmount(paymentDTO.getBaseAmount())
                    .discountApplied(paymentDTO.getDiscountApplied())
                    .finalAmount(paymentDTO.getFinalAmount())
                    .paymentMethod(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.valueOf(paymentDTO.getPaymentMethod()))
                    .paymentStatus(com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus.valueOf(paymentDTO.getPaymentStatus()))
                    .client(client)
                    .build();
        }
        paymentRepository.save(payment);
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
     * [MEJORA JUNIOR] Proceso programado para marcar pagos vencidos.
     * Marca un pago como vencido si ya pasó la fecha límite de pago de su período.
     * 
     * @param today Fecha actual de referencia para evaluar los vencimientos.
     */
    public void markOverduePayments(LocalDate today) {
        // [MEJORA JUNIOR] En lugar de obtener todos los pagos pendientes y recorrerlos
        // en un bucle for() (lo que genera una consulta SELECT y múltiples UPDATE por
        // cada pago),
        // llamamos a la query masiva que actualiza todo directamente en la base de
        // datos de una vez (Bulk Update). Esto reduce el tráfico de red e I/O en BD.
        paymentRepository.markOverduePaymentsBulk(today);
    }
}
