package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

/**
 * [MEJORA JUNIOR] Entidad JPA que representa un Pago de cuota/membresía realizado por un Socio.
 * 
 * Cambios arquitectónicos realizados:
 * 1. Vinculación Directa con Plan (plan_id): Se añadió la relación @ManyToOne con Plan.
 *    Esto congela la "fotografía del plan pagado" en el momento exacto del cobro. De este modo,
 *    si un cliente cambia de plan en el futuro, sus recibos e historial de pagos anteriores no sufren alteraciones.
 * 2. Referencia a Socio (client_id): Apunta al ID surrogate (id_client) de la tabla 'clients'.
 * 3. Restricción Única (uc_client_period): Garantiza que no se pueda cobrar dos veces el mismo mes/período al mismo socio.
 * 4. Remoción de Lombok @Data: Se usan @Getter, @Setter y @EqualsAndHashCode(of = "id") explícitos para Hibernate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments", uniqueConstraints = {
    @UniqueConstraint(name = "uc_client_period", columnNames = {"client_id", "period"})
})
@ToString(exclude = {"client", "plan"})
@EqualsAndHashCode(of = "id")
public class Payment {

    /**
     * Identificador único del pago en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    /**
     * Mes/período al cual corresponde la cuota pagada.
     */
    @NotNull(message = "El período de pago es obligatorio")
    @Column(name = "period", nullable = false)
    private LocalDate period;

    /**
     * Fecha real en la que se abonó o registró el pago.
     */
    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    /**
     * Monto base nominal asignado al cobro.
     */
    @NotNull(message = "El monto base es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto base no puede ser negativo")
    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseAmount;

    /**
     * Forma de pago utilizada (EFECTIVO, TRANSFERENCIA, MERCADO_PAGO, etc.).
     */
    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    /**
     * Estado del pago (CONFIRMADO, PENDIENTE, VENCIDO, CANCELADO).
     */
    @NotNull(message = "El estado de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    /**
     * Monto de descuento aplicado a la transacción.
     */
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Column(name = "discount_applied", precision = 10, scale = 2)
    private BigDecimal discountApplied;

    /**
     * Monto neto final cobra a abonar (Monto base - Descuento).
     */
    @NotNull(message = "El monto final es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto final no puede ser negativo")
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    /**
     * Socio al cual pertenece esta transacción de pago.
     */
    @NotNull(message = "El cliente asociado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * [MEJORA JUNIOR] Plan específico que se abonó en esta transacción.
     * Mantiene la integridad histórica sin depender del plan actual del cliente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

}
