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

/**
 * [MEJORA JUNIOR] Servicio para gestionar la lógica de negocio de los Clientes (Socios).
 * Cuenta con inyección de dependencias a través de Lombok (@RequiredArgsConstructor)
 * y transaccionalidad de Spring (@Transactional) para asegurar la consistencia en base de datos.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final HistoricalPlanService historicalPlanService;

    /**
     * [MEJORA JUNIOR] Obtiene la lista completa de socios mapeada a DTOs.
     * Mapeamos a DTO para no exponer las entidades JPA directamente a las vistas.
     */
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

    /**
     * [MEJORA JUNIOR] Guarda o actualiza un socio.
     * Si es una edición y el plan cambió, automáticamente llama a HistoricalPlanService
     * para cerrar el historial actual y registrar la vigencia del nuevo plan.
     */
    public void save(Client client) {
        // [MEJORA JUNIOR] Verificamos si el DNI ya existe en base de datos
        boolean isNew = !clientRepository.existsById(client.getDocumentId());

        if (!isNew) {
            // [MEJORA JUNIOR] Si ya existe, obtenemos la versión actual para comparar planes
            Client existing = clientRepository.findById(client.getDocumentId()).orElseThrow();

            if (!existing.getCurrentPlan().equals(client.getCurrentPlan())) {
                // [MEJORA JUNIOR] Caso: Cambio de plan de entrenamiento
                // 1. Cerramos el historial anterior poniendo fecha de fin a hoy
                historicalPlanService.closeCurrentPlan(existing);
                // 2. Guardamos el cliente con su nuevo plan
                clientRepository.save(client);
                // 3. Abrimos un nuevo registro en el historial de planes
                historicalPlanService.registerNewPlan(client, client.getCurrentPlan());
            } else {
                // [MEJORA JUNIOR] Caso: Solo se actualizaron datos básicos (teléfono, nombre, etc.)
                clientRepository.save(client);
            }
        } else {
            // [MEJORA JUNIOR] Caso: Es un socio totalmente nuevo en el gimnasio
            // 1. Persistimos los datos del nuevo cliente primero
            clientRepository.save(client);
            // 2. Registramos su plan inicial en el historial
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
