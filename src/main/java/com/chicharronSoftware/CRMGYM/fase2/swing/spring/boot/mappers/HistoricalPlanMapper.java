package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.HistoricalPlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import org.springframework.stereotype.Component;

@Component
public class HistoricalPlanMapper {

    public static HistoricalPlanDTO toDTO(HistoricalPlan historicalPlan) {
        return new HistoricalPlanDTO(
                historicalPlan.getIdHistorical(),
                historicalPlan.getStartDate(),
                historicalPlan.getEndDate(),
                historicalPlan.getIsActive() ? "Activo" : "Inactivo",
                historicalPlan.getClient().getName() + " " + historicalPlan.getClient().getLastName(),
                historicalPlan.getPlan().getNamePlan()
        );
    }

    public static HistoricalPlan toEntity(HistoricalPlanDTO dto, Client client, Plan plan) {
        return HistoricalPlan.builder()
                .idHistorical(dto.getIdHistorical())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive("Activo".equalsIgnoreCase(dto.getIsActive()))
                .client(client)
                .plan(plan)
                .build();
    }


}
