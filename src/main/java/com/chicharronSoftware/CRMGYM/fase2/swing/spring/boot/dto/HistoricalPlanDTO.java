package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricalPlanDTO {

    private Long idHistorical;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isActive; // "Activo" o "Inactivo"
    private String documentId;
    private String clientName; // client.getName() + " " + client.getLastName()
    private String planName; // plan.getNamePlan()
}
