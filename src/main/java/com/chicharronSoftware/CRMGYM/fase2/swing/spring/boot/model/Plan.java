package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * [MEJORA JUNIOR] Entidad JPA que representa un Plan de Entrenamiento en CRMGYM.
 * 
 * Cambios arquitectónicos realizados:
 * 1. Remoción de Lombok @Data: Se utilizan @Getter y @Setter explícitos junto con
 *    @EqualsAndHashCode(of = "idPlan") para evitar problemas al comparar colecciones en Hibernate.
 * 2. Eliminación de la colección @OneToMany 'historicalPlans': Las búsquedas históricas se realizan
 *    a través de HistoricalPlanRepository, aligerando la carga de entidades Plan en memoria.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "idPlan")
@Entity
@Builder
@Table(name = "plans")
public class Plan {

    /**
     * Identificador único autogenerado del plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan", nullable = false)
    private Integer idPlan;

    /**
     * Nombre único del plan (ej. "Musculación", "Pase Libre", "Sin Plan").
     */
    @NotBlank(message = "El nombre del plan es obligatorio")
    @Column(name = "name_plan", nullable = false, unique = true)
    private String namePlan;

    /**
     * Cantidad de días habilitados a la semana para este plan.
     */
    @NotNull(message = "Los días habilitados son obligatorios")
    @Min(value = 1, message = "Los días habilitados deben ser al menos 1")
    @Column(name = "days_enabled", nullable = false)
    private Integer daysEnabled;

    /**
     * Cantidad de horas permitidas por sesión o semana.
     */
    @NotNull(message = "Las horas habilitadas son obligatorias")
    @Min(value = 1, message = "Las horas habilitadas deben ser al menos 1")
    @Column(name = "hours_enabled", nullable = false)
    private Integer hoursEnabled;

    /**
     * Costo mensual o tarifa del plan.
     */
    @NotNull(message = "El valor del plan es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor debe ser mayor a 0")
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    /**
     * Observaciones o notas adicionales del plan.
     */
    @Column(name = "notes")
    private String notes;

    /**
     * Estado del plan (true = Disponible para asignación, false = Inactivo/Desactivado).
     */
    @NotNull(message = "El estado del plan es obligatorio")
    @Column(name = "status")
    private Boolean isActive;

    public Plan(String namePlan, Integer daysEnabled, Integer hoursEnabled, BigDecimal value, String notes, Boolean isActive) {
        this.namePlan = namePlan;
        this.daysEnabled = daysEnabled;
        this.hoursEnabled = hoursEnabled;
        this.value = value;
        this.notes = notes;
        this.isActive = isActive;
    }
}
