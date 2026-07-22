package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;

/**
 * [MEJORA JUNIOR] Mapeador de conversión entre la entidad Payment y el objeto de transferencia PaymentDTO.
 */
public class PaymentMapper {

    /**
     * Convierte una entidad Payment a su representación PaymentDTO.
     * Prioriza el plan asociado directamente al pago (fotografía histórica de la transacción).
     */
    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        // [MEJORA JUNIOR] Obtenemos el nombre del plan directamente de la transacción.
        // Si no está seteado, utilizamos el plan actual del cliente como respaldo.
        String planName = "";
        if (payment.getPlan() != null) {
            planName = payment.getPlan().getNamePlan();
        } else if (payment.getClient() != null && payment.getClient().getCurrentPlan() != null) {
            planName = payment.getClient().getCurrentPlan().getNamePlan();
        }

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
                .namePlan(planName)
                .build();
    }

    public static Payment toEntity(PaymentDTO dto, Client client) {
        return toEntity(dto, client, client != null ? client.getCurrentPlan() : null);
    }

    public static Payment toEntity(PaymentDTO dto, Client client, Plan plan) {
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
                .plan(plan)
                .build();
    }

}
