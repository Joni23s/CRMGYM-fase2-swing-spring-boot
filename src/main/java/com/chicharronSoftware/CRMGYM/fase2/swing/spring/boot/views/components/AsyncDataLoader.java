package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Clase de utilidad genérica para cargar datos asincrónicamente en paneles.
 * Envuelve la lógica repetitiva del SwingWorker y previene la congelación de la UI.
 */
public class AsyncDataLoader {

    public interface DataLoadCallback<T> {
        void onSuccess(T result);
        void onError(Exception ex);
    }

    /**
     * Ejecuta una carga de datos en segundo plano y devuelve los resultados en el EDT.
     * @param loader La función que carga los datos (ej. consulta a BD).
     * @param callback Callback para manejar el resultado o el error en el EDT.
     * @param <T> El tipo de dato devuelto.
     */
    public static <T> void loadData(Callable<T> loader, DataLoadCallback<T> callback) {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                // [MEJORA JUNIOR] Este método se ejecuta en un hilo separado (Background Thread).
                // Aquí es seguro hacer peticiones a la BD sin congelar la ventana.
                return loader.call();
            }

            @Override
            protected void done() {
                // [MEJORA JUNIOR] Este método se ejecuta de vuelta en el EDT (Event Dispatch Thread).
                // Aquí es seguro interactuar con los componentes visuales (JTable, JLabel, etc.).
                try {
                    T result = get();
                    callback.onSuccess(result);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        };
        worker.execute();
    }
}
