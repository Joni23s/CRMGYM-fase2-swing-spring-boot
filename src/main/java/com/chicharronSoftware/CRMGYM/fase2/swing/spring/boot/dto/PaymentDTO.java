package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private String idPayment;

    private String period;

    private String paymentDate;

    private String baseAmount;

    private String paymentMethod;

    private String paymentStatus;

    private String discountApplied;

    private String finalAmount;

    private String documentId;

    private String nameClient;

    private String namePlan;

}
