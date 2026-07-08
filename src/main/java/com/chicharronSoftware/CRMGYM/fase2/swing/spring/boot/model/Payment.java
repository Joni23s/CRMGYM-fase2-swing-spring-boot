package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
// [MEJORA JUNIOR] Agregamos una restricción única (uniqueConstraints) en la base de datos
// para garantizar que no se inserte más de un pago para el mismo 'cliente' y 'período'.
// Si alguien intenta cobrar dos veces el mismo mes, la BD lanzará un error y lo bloquearemos.
@Table(name = "payments", uniqueConstraints = {
    @UniqueConstraint(name = "uc_client_period", columnNames = {"document_id", "period"})
})
@ToString(exclude = "client")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    // Fecha a la que corresponde el pago (ejemplo: mes de agosto)
    // [MEJORA JUNIOR] @NotNull asegura que antes de guardar a la BD, Spring valide que este dato exista.
    @NotNull(message = "El período de pago es obligatorio")
    @Column(name = "period", nullable = false)
    private LocalDate period;

    // Fecha real en la que se efectuó el pago
    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    // [MEJORA JUNIOR] @DecimalMin garantiza que el monto base no sea negativo.
    @NotNull(message = "El monto base es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto base no puede ser negativo")
    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseAmount;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @NotNull(message = "El estado de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Column(name = "discount_applied", precision = 10, scale = 2)
    private BigDecimal discountApplied;

    @NotNull(message = "El monto final es obligatorio")
    @DecimalMin(value = "0.0", message = "El monto final no puede ser negativo")
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    // Relación con Cliente
    @NotNull(message = "El cliente asociado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Client client;

}
