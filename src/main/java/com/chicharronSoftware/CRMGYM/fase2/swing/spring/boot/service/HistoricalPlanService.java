package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.HistoricalPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricalPlanService {

    @Autowired
    private HistoricalPlanRepository historicalPlanRepository;

    public List<HistoricalPlan> findByClient(Client client){
        return historicalPlanRepository.findByClient(client);
    }

    public List<HistoricalPlan> findByPlan(Plan plan){
        return historicalPlanRepository.findByPlan(plan);
    }

    public List<HistoricalPlan> findByIsActive(boolean status){
        return historicalPlanRepository.findByIsActive(status);
    }

    public void deactivate(Long id) {
        historicalPlanRepository.findById(id).ifPresent(h -> {
            h.setIsActive(false);
            historicalPlanRepository.save(h);
        });
    }

    public void activate(Long id) {
        historicalPlanRepository.findById(id).ifPresent(h -> {
            h.setIsActive(true);
            historicalPlanRepository.save(h);
        });
    }
}
