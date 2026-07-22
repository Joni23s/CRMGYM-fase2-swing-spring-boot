package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * [MEJORA JUNIOR] Repositorio Spring Data JPA para la entidad Payment (Pagos).
 * 
 * Optimización N+1:
 * - Sobrescribimos y anotamos las consultas con @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
 *   para forzar un LEFT JOIN unificado en la consulta base. Esto nos trae al cliente, su plan actual y el plan específico abonado
 *   en un solo viaje a la base de datos, previniendo decenas de consultas secundarias (patrón N+1).
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Override
    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    List<Payment> findAll();

    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    List<Payment> findByClient_DocumentId(int documentId);

    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    List<Payment> findByClient_DocumentIdOrderByPaymentDateDesc(Integer clientId);

    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    Optional<Payment> findTopByClient_DocumentIdOrderByPaymentDateDesc(Integer clientId);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0) FROM Payment p WHERE p.paymentStatus = com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus.CONFIRMADO")
    java.math.BigDecimal sumTotalConfirmedRevenue();

    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    List<Payment> findTop4ByOrderByIdDesc();

    @EntityGraph(attributePaths = {"client", "client.currentPlan", "plan"})
    List<Payment> findTop3ByPaymentStatusInOrderByPeriodAsc(List<PaymentStatus> paymentStatuses);

    /**
     * [MEJORA JUNIOR] Actualización masiva en lote (Bulk Update).
     * Modifica el estado de los pagos vencidos directamente en el motor SQL sin cargarlos en memoria.
     */
    @Modifying
    @Query("UPDATE Payment p SET p.paymentStatus = 'VENCIDO' WHERE p.paymentStatus = 'PENDIENTE' AND p.period < :today")
    int markOverduePaymentsBulk(LocalDate today);
}
