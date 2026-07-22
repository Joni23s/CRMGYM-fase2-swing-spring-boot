package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * [MEJORA JUNIOR] Clase base abstracta para los Presentadores de la arquitectura MVP.
 * Centraliza la ejecución en segundo plano (AsyncDataLoader), el bloqueo/desbloqueo
 * temporal de botones de acción para evitar envíos múltiples y la captura limpia de errores.
 */
public abstract class BasePresenter {

    /**
     * [MEJORA JUNIOR] Ejecuta una tarea asíncrona en segundo plano deshabilitando
     * automáticamente los componentes de UI recibidos hasta que finalice la operación.
     * 
     * @param task Tarea a ejecutar en segundo plano (Worker Thread).
     * @param onSuccess Callback invocado en el hilo de la UI (EDT) al completar exitosamente.
     * @param controlsToDisable Lista varargs de botones o controles a deshabilitar temporalmente.
     */
    protected <T> void executeAsync(Callable<T> task, Consumer<T> onSuccess, JComponent... controlsToDisable) {
        setControlsEnabled(false, controlsToDisable);

        AsyncDataLoader.loadData(
            task,
            new AsyncDataLoader.DataLoadCallback<T>() {
                @Override
                public void onSuccess(T result) {
                    setControlsEnabled(true, controlsToDisable);
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    setControlsEnabled(true, controlsToDisable);
                    handleError(ex);
                }
            }
        );
    }

    /**
     * Habilita o deshabilita un grupo de componentes gráficos de Swing.
     */
    protected void setControlsEnabled(boolean enabled, JComponent... controls) {
        if (controls != null) {
            for (JComponent control : controls) {
                if (control != null) {
                    control.setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Captura y muestra errores formateados al usuario en la interfaz gráfica.
     */
    protected void handleError(Exception ex) {
        String message = ex instanceof IllegalArgumentException ? ex.getMessage() : "Ocurrió un error: " + ex.getMessage();
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
