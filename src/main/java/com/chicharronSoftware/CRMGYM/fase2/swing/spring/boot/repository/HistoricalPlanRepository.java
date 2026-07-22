package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * [MEJORA JUNIOR] Repositorio Spring Data JPA para la entidad HistoricalPlan (Historial de Planes).
 * 
 * Permite consultar la auditoría de cambios de planes de los socios en el tiempo.
 * Se utilizan JOIN FETCH explícitos para traer la entidad Client y Plan cargadas e impedir la N+1.
 */
@Repository
public interface HistoricalPlanRepository extends JpaRepository<HistoricalPlan, Long> {

    List<HistoricalPlan> findByIsActive(boolean isActive);

    Optional<HistoricalPlan> findFirstByClientAndIsActiveTrueOrderByStartDateDesc(Client client);

    @Query("""
    SELECT hp
    FROM HistoricalPlan hp
    JOIN FETCH hp.client c
    JOIN FETCH hp.plan p
    WHERE c IN :clients
""")
    List<HistoricalPlan> findByClientsWithDetails(@Param("clients") List<Client> clients);

    @Query("""
    SELECT hp
    FROM HistoricalPlan hp
    JOIN FETCH hp.client c
    JOIN FETCH hp.plan p
""")
    List<HistoricalPlan> findAllWithDetails();

    @Query("""
    SELECT hp
    FROM HistoricalPlan hp
    JOIN FETCH hp.client c
    JOIN FETCH hp.plan p
    WHERE c.documentId = :documentId
""")
    List<HistoricalPlan> findByClientWithDetails(@Param("documentId") Integer documentId);

}
