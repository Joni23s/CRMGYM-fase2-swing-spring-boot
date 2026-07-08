package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.config;

import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Gestor centralizado de hilos para operaciones asíncronas de la aplicación.
 * Mantiene un pool de hilos reutilizables para evitar la sobrecarga de crear hilos.
 */
@Component
public class ExecutorServiceManager {

    // Pool fijo de hilos para balancear carga de trabajo sin saturar CPU
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * Ejecuta una tarea en segundo plano usando el pool de hilos.
     * @param task Tarea a ejecutar que devuelve un resultado.
     * @param <T> Tipo de resultado.
     * @return Future que permite obtener el resultado o cancelar la tarea.
     */
    public <T> Future<T> executeInBackground(Callable<T> task) {
        // [MEJORA JUNIOR] Delegamos la ejecución al ExecutorService. Esto se usa para
        // lógica pesada en backend que no está directamente atada a la UI.
        return executor.submit(task);
    }

    /**
     * Ejecuta una tarea en segundo plano sin esperar un resultado.
     * @param task Tarea a ejecutar.
     */
    public void executeInBackground(Runnable task) {
        executor.submit(task);
    }

    @PreDestroy
    public void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
