package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * [MEJORA JUNIOR] Evento de Spring que representa la desactivación de un plan de entrenamiento.
 * Se utiliza para notificar de forma asíncrona o desacoplada a otros módulos del sistema
 * (como el de Clientes) para que realicen las acciones de limpieza correspondientes.
 */
@Getter
public class PlanDeactivatedEvent extends ApplicationEvent {
    private final String planName;

    public PlanDeactivatedEvent(Object source, String planName) {
        super(source);
        this.planName = planName;
    }
}
