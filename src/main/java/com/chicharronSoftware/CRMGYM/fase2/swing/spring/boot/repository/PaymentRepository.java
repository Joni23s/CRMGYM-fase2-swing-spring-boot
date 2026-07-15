package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // [MEJORA JUNIOR] Sobrescribimos findAll para traer los objetos "client" y su "currentPlan"
    // en una sola consulta JOIN. Esto evita que Hibernate intente buscarlos de forma perezosa (LAZY)
    // fuera de la transacción, previniendo el error LazyInitializationException.
    @Override
    @EntityGraph(attributePaths = {"client", "client.currentPlan"})
    List<Payment> findAll();

    // Buscar pagos de un cliente por DNI
    @EntityGraph(attributePaths = {"client", "client.currentPlan"})
    List<Payment> findByClient_DocumentId(int documentId);

    // Buscar pagos por estado (ejemplo: PENDING, COMPLETED, OVERDUE)
    @EntityGraph(attributePaths = {"client", "client.currentPlan"})
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    @EntityGraph(attributePaths = {"client", "client.currentPlan"})
    List<Payment> findByClient_DocumentIdOrderByPaymentDateDesc(Integer clientId);

    @EntityGraph(attributePaths = {"client", "client.currentPlan"})
    Optional<Payment> findTopByClient_DocumentIdOrderByPaymentDateDesc(Integer clientId);

    // [MEJORA JUNIOR] Actualización masiva (bulk update).
    // Con @Modifying indicamos que esta query modificará datos. 
    // Usamos JPQL para actualizar todos los pagos pendientes y vencidos en una sola consulta
    // en vez de traerlos a memoria e iterar sobre ellos para guardarlos uno a uno.
    @Modifying
    @Query("UPDATE Payment p SET p.paymentStatus = 'VENCIDO' WHERE p.paymentStatus = 'PENDIENTE' AND p.period < :today")
    int markOverduePaymentsBulk(LocalDate today);
}
