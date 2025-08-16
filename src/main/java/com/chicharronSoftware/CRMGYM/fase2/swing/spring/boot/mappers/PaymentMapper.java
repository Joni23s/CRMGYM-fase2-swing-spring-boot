package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        return PaymentDTO.builder()
                .idPayment(payment.getId() != null ? payment.getId().toString() : "")
                .period(payment.getPeriod() != null ? payment.getPeriod().format(DATE_FORMATTER) : "")
                .paymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate().format(DATE_FORMATTER) : "")
                .baseAmount(payment.getBaseAmount() != null ? formatMoney(payment.getBaseAmount()) : "")
                .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "")
                .paymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : "")
                .discountApplied(payment.getDiscountApplied() != null ? formatMoney(payment.getDiscountApplied()) : "")
                .finalAmount(payment.getFinalAmount() != null ? formatMoney(payment.getFinalAmount()) : "")
                .documentId(payment.getClient() != null ? String.valueOf(payment.getClient().getDocumentId()) : "")
                .nameClient(payment.getClient() != null ? payment.getClient().getName() + " " + payment.getClient().getLastName() : "")
                .namePlan(payment.getClient() != null && payment.getClient().getCurrentPlan() != null
                        ? payment.getClient().getCurrentPlan().getNamePlan() : "")
                .build();
    }

    private static String formatMoney(java.math.BigDecimal amount) {
        return "$ " + amount.setScale(2, RoundingMode.HALF_UP);
    }

    // --- Opcional: si necesitás reconstruir la entidad desde el DTO ---
    // Nota: acá deberías resolver el client desde un servicio o repo por documentId

    public static Payment toEntity(PaymentDTO dto, Client client) {
        if (dto == null) return null;

        return Payment.builder()
                .id(dto.getIdPayment() != null ? Long.valueOf(dto.getIdPayment()) : null)
                .period(LocalDate.parse(dto.getPeriod(), DATE_FORMATTER))
                .paymentDate(LocalDate.parse(dto.getPaymentDate(), DATE_FORMATTER))
                .baseAmount(new BigDecimal(dto.getBaseAmount().replace("$", "").trim()))
                .paymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()))
                .paymentStatus(PaymentStatus.valueOf(dto.getPaymentStatus()))
                .discountApplied(dto.getDiscountApplied() != null && !dto.getDiscountApplied().isEmpty()
                        ? new BigDecimal(dto.getDiscountApplied().replace("$", "").trim())
                        : null)
                .finalAmount(new BigDecimal(dto.getFinalAmount().replace("$", "").trim()))
                .client(client)
                .build();
    }

}
