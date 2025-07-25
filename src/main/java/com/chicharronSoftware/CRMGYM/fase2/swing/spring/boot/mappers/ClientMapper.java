package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMapper {

    public ClientDTO toDTO(Client client) {
        String planName = (client.getCurrentPlan() != null)
                ? client.getCurrentPlan().getNamePlan()
                : "Sin plan";

        return new ClientDTO(
                client.getDocumentId(),
                client.getName(),
                client.getLastName(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getIsActive() ? "Activo" : "Inactivo",
                planName
        );
    }


    public static Client toEntity(ClientDTO dto, List<HistoricalPlan> historicalPlans) {
        return new Client(
                dto.getDocumentId(),
                dto.getName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                "Activo".equals(dto.getStatus()),
                (historicalPlans != null && !historicalPlans.isEmpty())
                        ? historicalPlans.getLast().getPlan()
                        : null
        );
    }
}