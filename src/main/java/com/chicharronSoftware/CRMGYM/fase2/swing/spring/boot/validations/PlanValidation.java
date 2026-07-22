package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * [MEJORA JUNIOR] Validador de planes.
 * Inyecta PlanRepository (capa inferior) en lugar de PlanService (capa superior)
 * para mantener la direccionalidad descendente entre capas de arquitectura.
 */
@Component
public class PlanValidation {
    private final PlanRepository planRepository;

    @Autowired
    public PlanValidation(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return planRepository.findByNamePlanIgnoreCase(name.trim()).isEmpty();
    }

    public boolean isValidNameForUpdate(Long planId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }

        return planRepository.findByNamePlanIgnoreCase(newName.trim())
                .map(existingPlan -> existingPlan.getIdPlan().equals(planId.intValue()))
                .orElse(true);
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
