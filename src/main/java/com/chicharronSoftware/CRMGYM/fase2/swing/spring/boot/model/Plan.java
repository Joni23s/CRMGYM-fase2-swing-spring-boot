package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan", nullable = false)
    private Integer idPlan;

    @Column(name = "name_plan", nullable = false, unique = true)
    private String namePlan;

    @Column(name = "days_enabled", nullable = false)
    private Integer daysEnabled;

    @Column(name = "hours_enabled", nullable = false)
    private Integer hoursEnabled;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status")
    private Boolean isActive;

    @OneToMany(mappedBy = "plan", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<HistoricalPlan> historicalPlans;

    // Constructor manual útil para inicializar sin lista de historial
    public Plan(Integer idPlan, String namePlan, Integer daysEnabled, Integer hoursEnabled, BigDecimal value, String notes, Boolean isActive) {
        this.idPlan = idPlan;
        this.namePlan = namePlan;
        this.daysEnabled = daysEnabled;
        this.hoursEnabled = hoursEnabled;
        this.value = value;
        this.notes = notes;
        this.isActive = isActive;
    }
}
