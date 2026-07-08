package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    // [MEJORA JUNIOR] Validamos que el nombre no quede vacío en la aplicación.
    @NotBlank(message = "El nombre del plan es obligatorio")
    @Column(name = "name_plan", nullable = false, unique = true)
    private String namePlan;

    // [MEJORA JUNIOR] Validamos que los días mínimos habilitados sean al menos 1.
    @NotNull(message = "Los días habilitados son obligatorios")
    @Min(value = 1, message = "Los días habilitados deben ser al menos 1")
    @Column(name = "days_enabled", nullable = false)
    private Integer daysEnabled;

    @NotNull(message = "Las horas habilitadas son obligatorias")
    @Min(value = 1, message = "Las horas habilitadas deben ser al menos 1")
    @Column(name = "hours_enabled", nullable = false)
    private Integer hoursEnabled;

    // [MEJORA JUNIOR] Validamos que el valor no sea negativo.
    @NotNull(message = "El valor del plan es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor debe ser mayor a 0")
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "notes")
    private String notes;

    @NotNull(message = "El estado del plan es obligatorio")
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

    public Plan(String namePlan, Integer daysEnabled, Integer hoursEnabled, BigDecimal value, String notes, Boolean isActive) {
        this.namePlan = namePlan;
        this.daysEnabled = daysEnabled;
        this.hoursEnabled = hoursEnabled;
        this.value = value;
        this.notes = notes;
        this.isActive = isActive;
    }
}
