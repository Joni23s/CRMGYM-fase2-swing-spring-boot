package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.ClientMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final HistoricalPlanService historicalPlanService;

    public List<ClientDTO> getAllClientsDTO() {
        return clientRepository.findAll()
                .stream()
                .map(ClientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<Client> findById(Integer documentId) {
        return clientRepository.findById(documentId);
    }

    public List<Client> findByName(String name) {
        return clientRepository.findByNameIgnoreCase(name);
    }

    public List<Client> findByLastName(String lastName) {
        return clientRepository.findByLastNameIgnoreCase(lastName);
    }

    public List<Client> findByIsActive(boolean isActive) {
        return clientRepository.findByIsActive(isActive);
    }

    public List<ClientDTO> findByIsActiveDTO(boolean isActive) {
        return clientRepository.findByIsActive(isActive)
                .stream()
                .map(ClientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void save(Client client) {
        boolean isNew = !clientRepository.existsById(client.getDocumentId());

        if (!isNew) {
            Client existing = clientRepository.findById(client.getDocumentId()).orElseThrow();

            if (!existing.getCurrentPlan().equals(client.getCurrentPlan())) {
                // Cerrar el historial anterior
                historicalPlanService.closeCurrentPlan(existing);
                // Guardar cambios en cliente primero
                clientRepository.save(client);
                // Registrar nuevo historial
                historicalPlanService.registerNewPlan(client, client.getCurrentPlan());
            } else {
                // Si no cambia el plan, solo actualizamos
                clientRepository.save(client);
            }
        } else {
            // Guardar cliente primero
            clientRepository.save(client);
            // Registrar historial con el cliente persistido
            historicalPlanService.registerNewPlan(client, client.getCurrentPlan());
        }
    }

    public List<Client> findByPhoneNumber(String phone) {
        return clientRepository.findByPhoneNumber(phone);
    }

    public List<Client> findByEmail(String mail) {
        return clientRepository.findByEmail(mail);
    }

    public List<Client> findByCurrentPlan(String namePlan) {
        return clientRepository.findByCurrentPlan_NamePlan(namePlan);
    }
}
