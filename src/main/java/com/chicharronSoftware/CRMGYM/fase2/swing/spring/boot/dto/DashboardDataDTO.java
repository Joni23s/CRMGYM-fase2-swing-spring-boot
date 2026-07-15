package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDataDTO {
    private long activeClients;
    private long activePlans;
    private BigDecimal totalRevenue;
    private List<PaymentDTO> recentPayments;
    private List<PaymentDTO> pendingPayments;
    private List<PaymentDTO> recentPays;
}
