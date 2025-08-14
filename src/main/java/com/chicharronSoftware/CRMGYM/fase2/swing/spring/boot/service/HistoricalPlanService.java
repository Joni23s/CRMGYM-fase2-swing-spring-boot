package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.HistoricalPlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.HistoricalPlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.HistoricalPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricalPlanService {

    private final HistoricalPlanRepository historicalPlanRepository;

    public void closeCurrentPlan(Client client) {
        historicalPlanRepository
                .findFirstByClientAndIsActiveTrueOrderByStartDateDesc(client)
                .ifPresent(current -> {
                    current.setEndDate(LocalDate.now());
                    current.setIsActive(false);
                    historicalPlanRepository.save(current);
                });
    }

    public void registerNewPlan(Client client, Plan plan) {
        HistoricalPlan historical = HistoricalPlan.builder()
                .client(client)
                .plan(plan)
                .startDate(LocalDate.now())
                .isActive(true)
                .build();
        historicalPlanRepository.save(historical);
    }

    public List<HistoricalPlan> findByClient(Client client){
        return historicalPlanRepository.findByClient(client);
    }

    public List<HistoricalPlanDTO> findByClientDTO(Client client){
        return historicalPlanRepository.findByClient(client).stream()
                .map(HistoricalPlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<HistoricalPlan> findByPlan(Plan plan){
        return historicalPlanRepository.findByPlan(plan);
    }

    public List<HistoricalPlan> findByIsActive(boolean status){
        return historicalPlanRepository.findByIsActive(status);
    }

    public boolean deactivate(Long id) {
        return historicalPlanRepository.findById(id).map(h -> {
            h.setIsActive(false);
            historicalPlanRepository.save(h);
            return true;
        }).orElse(false);
    }

    public boolean activate(Long id) {
        return historicalPlanRepository.findById(id).map(h -> {
            h.setIsActive(true);
            historicalPlanRepository.save(h);
            return true;
        }).orElse(false);
    }

    public List<HistoricalPlanDTO> findByClientsWithDetails(List<Client> clients) {
        return  historicalPlanRepository.findByClientsWithDetails(clients)
                .stream()
                .map(HistoricalPlanMapper::toDTO)
                .toList();

    }

    public List<HistoricalPlanDTO> findByClientWithDetails(String documentId) {
        return  historicalPlanRepository.findByClientWithDetails(documentId)
                .stream()
                .map(HistoricalPlanMapper::toDTO)
                .toList();

    }
}
