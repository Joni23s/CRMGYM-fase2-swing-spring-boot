package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.event;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.HistoricalPlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * [MEJORA JUNIOR] Oyente de eventos (EventListener) que actúa como coordinador
 * entre el Bounded Context de Planes y el Bounded Context de Socios/Clientes.
 * 
 * Permite que los servicios principales no se inyecten entre sí de forma cíclica
 * o acoplada, manteniendo la cohesión y la responsabilidad única.
 */
@Component
@RequiredArgsConstructor
public class PlanEventsListener {

    private final ClientService clientService;
    private final PlanService planService;
    private final HistoricalPlanService historicalPlanService;

    /**
     * Reacciona cuando un plan es desactivado en el sistema.
     * Busca a todos los clientes activos del plan desactivado, cierra su historial
     * de planes y los migra al plan comodín "Sin Plan".
     * 
     * @param event Información del plan desactivado.
     */
    @EventListener
    public void handlePlanDeactivated(PlanDeactivatedEvent event) {
        String planName = event.getPlanName();
        
        // [MEJORA JUNIOR] Flujo de actualización: 
        // 1. Buscamos todos los clientes vinculados al plan que se va a inactivar.
        List<Client> clients = clientService.findByCurrentPlan(planName);
        if (clients.isEmpty()) {
            return;
        }

        // [MEJORA JUNIOR] Cacheamos la búsqueda de "Sin Plan" fuera del loop de clientes
        // para prevenir N consultas repetitivas (N+1 queries en base de datos).
        Optional<Plan> noPlanOpt = planService.findByNamePlanIgnoreCase("Sin Plan");
        
        clients.forEach(client -> {
            // 2. Cerramos la vigencia del plan inactivo en el historial del cliente
            historicalPlanService.closeCurrentPlan(client);
            
            // 3. Reasignamos el plan al socio al plan por defecto
            noPlanOpt.ifPresent(noPlan -> {
                client.setCurrentPlan(noPlan);
                clientService.save(client);
            });
        });
    }
}
