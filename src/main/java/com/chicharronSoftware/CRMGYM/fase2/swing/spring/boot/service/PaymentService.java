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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    // --- Consultas comunes ---
    public List<Payment> findByClientDocumentId(int documentId) {
        return paymentRepository.findByClient_DocumentId(documentId);
    }

    public List<PaymentDTO> findByClientDocumentIdDTO(int documentId) {
        return findByClientDocumentId(documentId)
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    public List<Payment> findByPeriod(LocalDate period) {
        return paymentRepository.findByPeriod(period);
    }

    public List<Payment> findByPaymentDateBetween(LocalDate start, LocalDate end) {
        return paymentRepository.findByPaymentDateBetween(start, end);
    }

    public BigDecimal getTotalCollectedByPeriod(LocalDate period) {
        return paymentRepository.getTotalCollectedByPeriod(period);
    }

    // --- DTO Helpers ---
    public List<PaymentDTO> getAllPaymentsDTO() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO toDTO(Payment payment) {
        return PaymentMapper.toDTO(payment);
    }

    // --- Lógica de negocio ---
    /**
     * Registra un nuevo pago con cálculo de monto final.
     * Se espera que baseAmount y discountApplied ya estén definidos.
     */
    public Payment registerPayment(Client client, BigDecimal baseAmount, BigDecimal discount,
                                   LocalDate period, LocalDate paymentDate,
                                   PaymentStatus status, String method) {

        BigDecimal finalAmount = baseAmount.subtract(discount != null ? discount : BigDecimal.ZERO);

        Payment payment = Payment.builder()
                .client(client)
                .baseAmount(baseAmount)
                .discountApplied(discount)
                .finalAmount(finalAmount)
                .period(period)
                .paymentDate(paymentDate)
                .paymentStatus(status)
                .paymentMethod(Enum.valueOf(
                        com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod.class,
                        method))
                .build();

        return paymentRepository.save(payment);
    }


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
        List<Payment> pending = paymentRepository.findByPaymentStatus(PaymentStatus.PENDIENTE);
        for (Payment p : pending) {
            if (p.getPeriod().isBefore(today)) {
                p.setPaymentStatus(PaymentStatus.VENCIDO);
                paymentRepository.save(p);
            }
        }
    }
}
