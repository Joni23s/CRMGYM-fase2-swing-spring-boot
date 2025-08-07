package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.ClientMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public Optional<Plan> findByNamePlanIgnoreCase(String name){
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

    public List<Plan> findByIsActive(boolean isActive){
        return planRepository.findByIsActive(isActive);
    }

    public void deactivate(Integer id) {
        planRepository.findById(id).ifPresent(plan -> {
            plan.setIsActive(false);
            planRepository.save(plan);
        });
    }

    public void activate(Integer id) {
        planRepository.findById(id).ifPresent(plan -> {
            plan.setIsActive(true);
            planRepository.save(plan);
        });
    }

    public List<PlanDTO> getAllPlans() {
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
