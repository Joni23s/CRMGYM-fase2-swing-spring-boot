package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final ClientService clientService;
    private final HistoricalPlanService historicalPlanService;

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

    public List<Plan> findByIsActive(boolean isActive) {
        return planRepository.findByIsActive(isActive);
    }

    public void changeStatusWithClients(Integer id, boolean status) {
        planRepository.findById(id).ifPresent(plan -> {
            if (!status) {
                List<Client> clients = clientService.findByCurrentPlan(plan.getNamePlan());
                clients.forEach(client -> {
                    historicalPlanService.closeCurrentPlan(client);
                    findByNamePlanIgnoreCase("Sin Plan").ifPresent(noPlan -> {
                        client.setCurrentPlan(noPlan);
                        clientService.save(client);
                    });
                });
            }
            plan.setIsActive(status);
            planRepository.save(plan);
        });
    }

    public List<PlanDTO> getAllPlansDTO() {
        return planRepository.findAll()
                .stream()
                .map(PlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<PlanDTO> findByIsActiveDTO(boolean isActive) {
        return planRepository.findByIsActive(isActive)
                .stream()
                .map(PlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void save(Plan plan) {
        planRepository.save(plan);
    }
}
