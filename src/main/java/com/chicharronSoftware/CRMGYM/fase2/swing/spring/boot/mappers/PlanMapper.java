package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    public PlanDTO toDTO(Plan plan) {
        return new PlanDTO(
                plan.getIdPlan(),
                plan.getNamePlan(),
                plan.getDaysEnabled(),
                plan.getHoursEnabled(),
                plan.getValue(),
                plan.getNotes(),
                (plan.getIsActive() ? "Activo" : "Inactivo")
        );
    }

    public static Plan toEntity(PlanDTO dto) {
        return new Plan(
                dto.getIdPlan(),
                dto.getNamePlan(),
                dto.getDaysEnabled(),
                dto.getHoursEnabled(),
                dto.getValue(),
                dto.getNotes(),
                "Activo".equalsIgnoreCase(dto.getStatus())
        );
    }
}