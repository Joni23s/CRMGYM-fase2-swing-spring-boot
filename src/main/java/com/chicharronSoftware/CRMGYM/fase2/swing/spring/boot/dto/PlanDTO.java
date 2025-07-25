package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDTO {

    private int idPlan;
    private String namePlan;
    private int daysEnabled;
    private int hoursEnabled;
    private BigDecimal value;
    private String notes;
    private String status; // "Activo" o "Inactivo"

    @Override
    public String toString() {
        return namePlan; // Esto es lo que va a mostrar el combo
    }

}
