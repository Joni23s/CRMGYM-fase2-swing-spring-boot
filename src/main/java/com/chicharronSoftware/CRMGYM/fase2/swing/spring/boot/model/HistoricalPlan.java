package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * [MEJORA JUNIOR] Entidad JPA que audita el historial de suscripciones y planes asignados a los clientes a lo largo del tiempo.
 * 
 * Propósito de Auditoría:
 * - Guarda un registro por cada período que un socio perteneció a un plan determinado.
 * - Los planes activos de un cliente tendrán 'endDate = null' e 'isActive = true'.
 * - Cuando un cliente cambia de plan, se asigna la fecha de fin ('endDate') al registro actual
 *   y se inserta una nueva fila con la nueva fecha de inicio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"client", "plan"})
@EqualsAndHashCode(of = "idHistorical")
@Builder
@Entity
@Table(name = "historical_plans")
public class HistoricalPlan {

    /**
     * Identificador autoincremental de la entrada del historial.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historical")
    private Long idHistorical;

    /**
     * Fecha de inicio de la vigencia de este plan para el socio.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Fecha de finalización del plan. Si es null, indica que el plan sigue abierto/vigente.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Estado activo/inactivo del registro en el historial.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Socio al cual pertenece este registro histórico.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Plan asociado al registro en dicho período.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plan", nullable = false)
    private Plan plan;

}
