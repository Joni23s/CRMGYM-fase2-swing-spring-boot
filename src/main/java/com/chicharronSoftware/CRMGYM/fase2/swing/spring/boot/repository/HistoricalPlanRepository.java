package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricalPlanRepository extends JpaRepository<HistoricalPlan, Long> {

    List<HistoricalPlan> findByClient(Client client);
    List<HistoricalPlan> findByPlan(Plan plan);
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
    WHERE c.documentId = :documentId
""")
    List<HistoricalPlan> findByClientWithDetails(@Param("documentId") String documentId);


}
