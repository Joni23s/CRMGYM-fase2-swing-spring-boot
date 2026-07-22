package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.event.PlanDeactivatedEvent;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Optional<Plan> findByNamePlanIgnoreCase(String name) {
        return planRepository.findByNamePlanIgnoreCase(name);
    }

    public Optional<Plan> findById(int id) {
        return planRepository.findById(id);
    }

    public List<Plan> findByHoursEnabled(int id) {
        return planRepository.findByHoursEnabled(id);
    }

    public List<Plan> findByDaysEnabled(int id) {
        return planRepository.findByDaysEnabled(id);
    }

    public List<Plan> findByValue(BigDecimal cost) {
        return planRepository.findByValue(cost);
    }

    public void changeStatusWithClients(Integer id, boolean status) {
        planRepository.findById(id).ifPresent(plan -> {
            if (!status) {
                // [MEJORA JUNIOR] Flujo desacoplado:
                // En lugar de inyectar ClientService y modificar los socios aquí de forma síncrona,
                // disparamos un evento a la aplicación de Spring. Esto permite separar las responsabilidades
                // entre el módulo de Planes y el de Clientes, facilitando el mantenimiento.
                eventPublisher.publishEvent(new PlanDeactivatedEvent(this, plan.getNamePlan()));
            }
            plan.setIsActive(status);
            planRepository.save(plan);
        });
    }

    /**
     * [MEJORA JUNIOR] Obtiene la lista de planes completa en formato DTO.
     */
    public List<PlanDTO> getAllPlansDTO() {
        return planRepository.findAll()
                .stream()
                .map(PlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * [MEJORA JUNIOR] Filtra los planes activos o inactivos y los retorna en formato DTO.
     */
    public List<PlanDTO> findByIsActiveDTO(boolean isActive) {
        return planRepository.findByIsActive(isActive)
                .stream()
                .map(PlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * [MEJORA JUNIOR] Guarda o actualiza un plan a partir de su DTO.
     * Permite que la capa de presentación (Swing / Presenter) interactúe 100% libre de entidades JPA.
     * 
     * @param dto Objeto DTO con los datos cargados desde el formulario visual.
     * @return PlanDTO guardado y mapeado de vuelta.
     */
    public PlanDTO saveDTO(PlanDTO dto) {
        Plan plan = PlanMapper.toEntity(dto);
        Plan saved = planRepository.save(plan);
        return PlanMapper.toDTO(saved);
    }

    /**
     * [MEJORA JUNIOR] Realiza búsquedas combinadas por filtros de nombre, horas, días o tarifa.
     * Retorna el conjunto resultante convertido a DTOs.
     */
    public List<PlanDTO> searchPlansDTO(String name, int hours, int days, BigDecimal cost) {
        java.util.Set<Plan> plans = new java.util.HashSet<>();
        if (name != null && !name.isBlank()) {
            planRepository.findByNamePlanIgnoreCase(name).ifPresent(plans::add);
        }
        if (hours > 0) {
            plans.addAll(planRepository.findByHoursEnabled(hours));
        }
        if (days > 0) {
            plans.addAll(planRepository.findByDaysEnabled(days));
        }
        if (cost != null && cost.compareTo(BigDecimal.ZERO) != 0) {
            plans.addAll(planRepository.findByValue(cost));
        }
        return plans.stream().map(PlanMapper::toDTO).collect(Collectors.toList());
    }

    public void save(Plan plan) {
        planRepository.save(plan);
    }
}
