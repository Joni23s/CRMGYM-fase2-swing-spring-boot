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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [MEJORA JUNIOR] Servicio para administrar el historial de cambios de planes de los socios.
 * Permite registrar cuándo se inicia un nuevo plan y cerrar la vigencia de los planes anteriores.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HistoricalPlanService {

    private final HistoricalPlanRepository historicalPlanRepository;

    /**
     * [MEJORA JUNIOR] Cierra la vigencia del plan de entrenamiento que el socio tiene activo.
     * Le coloca fecha de fin (hoy) y marca su estado como inactivo (isActive = false).
     */
    public void closeCurrentPlan(Client client) {
        // [MEJORA JUNIOR] Buscamos el último plan activo registrado
        historicalPlanRepository
                .findFirstByClientAndIsActiveTrueOrderByStartDateDesc(client)
                .ifPresent(current -> {
                    current.setEndDate(LocalDate.now());
                    current.setIsActive(false);
                    // [MEJORA JUNIOR] Guardamos los cambios para registrar el fin de este plan
                    historicalPlanRepository.save(current);
                });
    }

    /**
     * [MEJORA JUNIOR] Registra la apertura de un nuevo plan para un cliente.
     * Comienza con fecha de inicio (hoy) y estado activo (isActive = true).
     */
    public void registerNewPlan(Client client, Plan plan) {
        HistoricalPlan historical = HistoricalPlan.builder()
                .client(client)
                .plan(plan)
                .startDate(LocalDate.now())
                .isActive(true)
                .build();
        historicalPlanRepository.save(historical);
    }

    /**
     * [MEJORA JUNIOR] Devuelve el historial detallado de planes asociados a una lista de clientes.
     */
    public List<HistoricalPlanDTO> findByClientsWithDetails(List<Client> clients) {
        return historicalPlanRepository.findByClientsWithDetails(clients)
                .stream()
                .map(HistoricalPlanMapper::toDTO)
                .toList();
    }

    /**
     * [MEJORA JUNIOR] Devuelve todo el historial del sistema mapeado a DTOs.
     */
    public List<HistoricalPlanDTO> findAllWithDetails() {
        return historicalPlanRepository.findAllWithDetails()
                .stream()
                .map(HistoricalPlanMapper::toDTO)
                .toList();
    }

    /**
     * [MEJORA JUNIOR] Devuelve el historial de un socio específico por DNI (Integer).
     */
    public List<HistoricalPlanDTO> findByClientWithDetails(Integer documentId) {
        return historicalPlanRepository.findByClientWithDetails(documentId)
                .stream()
                .map(HistoricalPlanMapper::toDTO)
                .toList();
    }

    /**
     * [MEJORA JUNIOR] Sobrecarga utilitaria para consultar por DNI en formato texto.
     */
    public List<HistoricalPlanDTO> findByClientWithDetails(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return List.of();
        }
        try {
            return findByClientWithDetails(Integer.parseInt(documentId.trim()));
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
}
