package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import jakarta.persistence.*;
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
