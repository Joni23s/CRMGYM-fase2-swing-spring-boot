package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.HistoricalPlan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalPlanRepository extends JpaRepository<HistoricalPlan, Long> {

    List<HistoricalPlan> findByClient(Client client);
    List<HistoricalPlan> findByPlan(Plan plan);
    List<HistoricalPlan> findByIsActive(boolean isActive);

}
