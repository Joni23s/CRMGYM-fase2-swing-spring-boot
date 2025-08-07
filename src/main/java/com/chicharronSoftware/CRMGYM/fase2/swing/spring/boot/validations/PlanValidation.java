package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class PlanValidation {
    private final Scanner console = new Scanner(System.in);
    private final PlanService planService;

    @Autowired
    public PlanValidation(PlanService planService) {
        this.planService = planService;
    }

    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return planService.findByNamePlanIgnoreCase(name.trim()).isEmpty();
    }

    public boolean isValidNameForUpdate(Long planId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }

        return planService.findByNamePlanIgnoreCase(newName.trim())
                .map(existingPlan -> existingPlan.getIdPlan().equals(planId)) // Si es el mismo plan, es válido
                .orElse(true); // Si no existe ningún plan con ese nombre, es válido
    }

    //Entendiendo que el gimnasio pueda abrir los 7 días de la semana
    public boolean isValidDays(int days) {
        return days >= 1 && days <= 7;
    }

    // Entendiendo que una persona pueda entrenar de 1 hasta 4 horas por día,
    // y un máximo de 7 días a la semana, entonces el total permitido va de 1 a 28 horas.
    public boolean isValidHours(int hours) {
        return hours >= 1 && hours <= 28;
    }

    public boolean isValidCost(BigDecimal cost) {
        if (cost == null) return false;

        // Debe ser mayor a cero
        if (cost.compareTo(BigDecimal.ZERO) <= 0) return false;

        // Debe tener como máximo 2 decimales
        if (cost.scale() > 2) return false;

        // No debe superar un monto arbitrario (ej: 100 mil)
        BigDecimal maxAllowed = new BigDecimal("100000");
        return cost.compareTo(maxAllowed) <= 0;
    }

}
