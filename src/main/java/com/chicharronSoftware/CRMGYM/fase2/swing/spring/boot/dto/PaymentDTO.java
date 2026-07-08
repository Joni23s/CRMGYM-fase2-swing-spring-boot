package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long idPayment;

    private LocalDate period;

    private LocalDate paymentDate;

    private BigDecimal baseAmount;

    private String paymentMethod;

    private String paymentStatus;

    private BigDecimal discountApplied;

    private BigDecimal finalAmount;

    private Integer documentId;

    private String nameClient;

    private String namePlan;

}
