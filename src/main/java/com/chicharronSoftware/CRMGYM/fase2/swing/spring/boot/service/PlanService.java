package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PlanMapper planMapper;

    public Optional<Plan> findByNamePlanIgnoreCase(String name){
        return planRepository.findByNamePlanIgnoreCase(name);
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
        return planRepository.findAll().stream()
                .map(planMapper::toDTO)
                .collect(Collectors.toList());
    }

}
