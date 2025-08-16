package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
