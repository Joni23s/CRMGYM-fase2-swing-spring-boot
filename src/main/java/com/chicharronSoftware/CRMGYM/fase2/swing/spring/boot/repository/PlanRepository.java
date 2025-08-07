package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    Optional<Plan> findByNamePlanIgnoreCase(String namePlan);
    List<Plan> findByIsActive(boolean isActive);
    Optional<Plan> findById(int id);
    List<Plan> findByHoursEnabled(int hours);
    List<Plan> findByDaysEnabled(int days);
    List<Plan> findByValue(BigDecimal cost);

}
