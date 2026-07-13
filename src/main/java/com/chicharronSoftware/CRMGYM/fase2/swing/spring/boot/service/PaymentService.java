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

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

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

    // --- Consultas comunes ---
    public List<Payment> findByClientDocumentId(int documentId) {
        return paymentRepository.findByClient_DocumentId(documentId);
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    // --- DTO Helpers ---
    public List<PaymentDTO> getAllPaymentsDTO() {
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
     * Marca un pago como vencido si ya pasó la fecha límite.
     */
    public void markOverduePayments(LocalDate today) {
        // [MEJORA JUNIOR] En lugar de obtener todos los pagos pendientes y recorrerlos
        // en un bucle for() (lo que genera una consulta SELECT y múltiples UPDATE por
        // cada pago),
        // llamamos a la query masiva que actualiza todo directamente en la base de
        // datos de una vez.
        paymentRepository.markOverduePaymentsBulk(today);
    }
}
