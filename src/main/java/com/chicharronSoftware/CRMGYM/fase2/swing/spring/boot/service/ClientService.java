package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.ClientMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
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
 * 
 * Flujo del Negocio:
 * - Gestiona el ciclo de vida del socio (alta, edición, deshabilitación).
 * - Mantiene sincronizado el plan activo ('currentPlan') y coordina el registro en HistoricalPlanService.
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

    /**
     * [MEJORA JUNIOR] Busca a un socio por su número de DNI (clave de negocio).
     */
    public Optional<Client> findById(Integer documentId) {
        return clientRepository.findByDocumentId(documentId);
    }

    /**
     * [MEJORA JUNIOR] Busca a un socio por su ID surrogate autogenerado en BD.
     */
    public Optional<Client> findByDatabaseId(Long id) {
        return clientRepository.findById(id);
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
     * para cerrar la vigencia del plan anterior y registrar el nuevo en el historial de auditoría.
     */
    public void save(Client client) {
        // [MEJORA JUNIOR] Buscamos si ya existe por ID o por DNI en base de datos
        Optional<Client> existingOpt = Optional.empty();
        if (client.getId() != null) {
            existingOpt = clientRepository.findById(client.getId());
        } else if (client.getDocumentId() != null) {
            existingOpt = clientRepository.findByDocumentId(client.getDocumentId());
        }

        if (existingOpt.isPresent()) {
            Client existing = existingOpt.get();
            // [MEJORA JUNIOR] Mantenemos el ID autogenerado si estamos editando por DNI
            if (client.getId() == null) {
                client.setId(existing.getId());
            }

            boolean planChanged = existing.getCurrentPlan() != null
                    ? !existing.getCurrentPlan().equals(client.getCurrentPlan())
                    : client.getCurrentPlan() != null;

            if (planChanged) {
                // [MEJORA JUNIOR] Caso: Cambio de plan de entrenamiento
                // 1. Cerramos el historial anterior poniendo fecha de fin a hoy
                historicalPlanService.closeCurrentPlan(existing);
                // 2. Guardamos el cliente con su nuevo plan
                clientRepository.save(client);
                // 3. Abrimos un nuevo registro en el historial de planes
                historicalPlanService.registerNewPlan(client, client.getCurrentPlan());
            } else {
                // [MEJORA JUNIOR] Caso: Solo se actualizaron datos personales (teléfono, mail, etc.)
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
