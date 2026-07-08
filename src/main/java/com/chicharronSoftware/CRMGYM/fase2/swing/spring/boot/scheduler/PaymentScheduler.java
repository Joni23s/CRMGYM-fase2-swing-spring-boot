package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.scheduler;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// [MEJORA JUNIOR] Esta clase se comporta como un componente de Spring y define tareas programadas.
// Usamos Lombok @RequiredArgsConstructor y @Slf4j para ahorrar código repetitivo de logs e inyección de dependencias.
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final PaymentService paymentService;

    // [MEJORA JUNIOR] Se ejecuta todos los días a la medianoche (cron: "segundo minuto hora día_del_mes mes día_de_la_semana").
    // "0 0 0 * * ?" se traduce como: 00:00:00 AM, todos los días de cualquier mes y año.
    @Scheduled(cron = "0 0 0 * * ?")
    public void markOverduePaymentsScheduled() {
        log.info("Iniciando tarea programada: Verificación diaria de pagos vencidos...");
        try {
            LocalDate today = LocalDate.now();
            paymentService.markOverduePayments(today);
            log.info("Tarea programada de pagos vencidos completada con éxito.");
        } catch (Exception e) {
            log.error("Ocurrió un error al ejecutar la tarea programada de pagos vencidos: {}", e.getMessage(), e);
        }
    }
}
