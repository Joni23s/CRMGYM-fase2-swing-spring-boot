package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Buscar pagos de un cliente por DNI
    List<Payment> findByClient_DocumentId(int documentId);

    // Buscar pagos por estado (ejemplo: PENDING, COMPLETED, OVERDUE)
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // Buscar pagos realizados en una fecha específica
    List<Payment> findByPaymentDate(LocalDate paymentDate);

    // Buscar pagos correspondientes a un período (mes)
    List<Payment> findByPeriod(LocalDate period);

    // Buscar pagos entre dos fechas (útil para informes)
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    // Buscar pagos de un cliente en un rango de fechas
    List<Payment> findByClient_DocumentIdAndPaymentDateBetween(int documentId, LocalDate start, LocalDate end);

    // Consulta personalizada (opcional): total recaudado en un período
    @Query("SELECT SUM(p.finalAmount) FROM Payment p WHERE p.period = :period")
    BigDecimal getTotalCollectedByPeriod(LocalDate period);

    List<Payment> findByClient_DocumentIdOrderByPaymentDateDesc(Integer clientId);

    Optional<Payment> findTopByClient_DocumentIdOrderByPaymentDateDesc(Integer clientId);
}
