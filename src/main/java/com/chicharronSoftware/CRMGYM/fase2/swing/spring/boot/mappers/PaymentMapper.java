package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;

public class PaymentMapper {

    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        return PaymentDTO.builder()
                .idPayment(payment.getId())
                .period(payment.getPeriod())
                .paymentDate(payment.getPaymentDate())
                .baseAmount(payment.getBaseAmount())
                .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "")
                .paymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : "")
                .discountApplied(payment.getDiscountApplied())
                .finalAmount(payment.getFinalAmount())
                .documentId(payment.getClient() != null ? payment.getClient().getDocumentId() : null)
                .nameClient(payment.getClient() != null ? payment.getClient().getName() + " " + payment.getClient().getLastName() : "")
                .namePlan(payment.getClient() != null && payment.getClient().getCurrentPlan() != null
                        ? payment.getClient().getCurrentPlan().getNamePlan() : "")
                .build();
    }

    public static Payment toEntity(PaymentDTO dto, Client client) {
        if (dto == null) return null;

        return Payment.builder()
                .id(dto.getIdPayment())
                .period(dto.getPeriod())
                .paymentDate(dto.getPaymentDate())
                .baseAmount(dto.getBaseAmount())
                .paymentMethod(dto.getPaymentMethod() != null && !dto.getPaymentMethod().isEmpty()
                        ? PaymentMethod.valueOf(dto.getPaymentMethod())
                        : null)
                .paymentStatus(dto.getPaymentStatus() != null && !dto.getPaymentStatus().isEmpty()
                        ? PaymentStatus.valueOf(dto.getPaymentStatus())
                        : null)
                .discountApplied(dto.getDiscountApplied())
                .finalAmount(dto.getFinalAmount())
                .client(client)
                .build();
    }

}
