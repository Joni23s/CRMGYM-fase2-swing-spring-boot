package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * =================================================================================
 * SIDEBAR NAVIGATION ITEM: BOTÓN REUTILIZABLE PARA LA BARRA LATERAL
 * =================================================================================
 * 
 * ---------------------------------------------------------------------------------
 * 1. EL ROL DE ButtonModel EN LOS ESTADOS INTERACTIVOS
 * ---------------------------------------------------------------------------------
 * En Swing, cada botón (`JButton`) posee un modelo de datos interno de tipo `ButtonModel`.
 * Este modelo realiza un seguimiento dinámico del estado del control interactivo en pantalla:
 * 
 * - Rollover (`isRollover()`): Identifica si el cursor del mouse está suspendido directamente
 *   sobre el componente, útil para pintar micro-animaciones o resaltados sutiles.
 * - Pressed (`isPressed()`): Indica si el usuario está haciendo click físico activo en el botón.
 * - Selected (`isSelected()`): Para botones de tipo toggle que mantienen un estado de selección.
 * 
 * En lugar de gestionar Listeners de mouse redundantes, consultamos el estado de este modelo
 * directamente durante el método de pintura, lo cual es más limpio, coherente y eficiente.
 * 
 * ---------------------------------------------------------------------------------
 * 2. DESACTIVAR EL PINTADO NATIVO: setContentAreaFilled(false)
 * ---------------------------------------------------------------------------------
 * Por defecto, los botones de Swing pintan un fondo rectangular gris y un borde biselado
 * nativo (según el Look and Feel del sistema operativo). Para diseñar un botón plano
 * moderno ("Flat Button") con esquinas redondeadas y transiciones sutiles:
 * 
 * - `setContentAreaFilled(false)`: Desactiva el rellenado rectangular nativo del fondo.
 * - `setBorderPainted(false)`: Evita que Swing intente dibujar el borde rectangular por defecto.
 * - `setFocusPainted(false)`: Remueve la línea de puntos alrededor del texto que denota el
 *   foco del teclado, permitiendo un diseño visual libre de interferencias.
 * 
 * Al apagar estos interruptores, el motor nativo de pintura cede todo el control a nuestras
 * rutinas personalizadas de pintado vectorial.
 * 
 * ---------------------------------------------------------------------------------
 * 3. ORDEN DEL CICLO DE PINTADO: ¿POR QUÉ super.paintComponent(g) VA AL FINAL?
 * ---------------------------------------------------------------------------------
 * La convención típica de Swing indica que al sobreescribir `paintComponent(Graphics g)`
 * se debe invocar a `super.paintComponent(g)` al principio de la función. Sin embargo, en
 * botones donde apagamos las propiedades nativas de fondo pero dejamos activo el pintado
 * de textos e iconos automáticos del framework, alterar este orden es crucial:
 * 
 *   1. Pintamos nuestras capas vectoriales primero (fondos redondeados, líneas de acento).
 *   2. Al final, invocamos `super.paintComponent(g)` para que Swing dibuje la capa superior:
 *      el texto del label (`JButton.getText()`) y el icono gráfico (`JButton.getIcon()`).
 * 
 * Si llamáramos a `super.paintComponent(g)` al principio, nuestro propio código de pintura
 * (como `fillRoundRect`) se dibujaría encima, cubriendo y ocultando el texto y los iconos,
 * dejándolos invisibles. Al llamarlo al final, Swing superpone la tipografía e icono
 * perfectamente en la capa superior ("z-order" de pintado).
 */
public class SidebarItem extends JButton {

    private final String iconType;
    private boolean active = false;

    /**
     * Constructor del item de navegación lateral.
     * 
     * @param text     Texto descriptivo de la opción de navegación.
     * @param iconType Nombre clave del icono vectorial a instanciar.
     */
    public SidebarItem(String text, String iconType) {
        super(text);
        this.iconType = iconType;

        // 1. Configuración de estilo y tipografía de acuerdo al Design System
        setFont(Theme.FONT_BODY);
        setForeground(Theme.TEXT_INACTIVE);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(14);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 2. Desactivar estilos nativos obstructivos
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        // 3. Vincular el recurso de icono vectorial por defecto
        setIcon(new VectorIcon(this.iconType, 18, Theme.TEXT_INACTIVE));

        // 4. Propiedades cliente de FlatLaf para consistencia estética
        putClientProperty("JButton.buttonType", "toolBarButton");
        putClientProperty("FlatLaf.style", "arc: 12; margin: 8 16 8 16;");
    }

    /**
     * Alterna de forma reactiva el estado de selección activa de este botón de navegación.
     * 
     * @param active true si el botón corresponde a la pantalla en visualización.
     */
    public void setActive(boolean active) {
        this.active = active;

        if (active) {
            setForeground(Theme.TEXT_ACTIVE);
            // Re-instanciar el icono con la tonalidad activa brillante
            setIcon(new VectorIcon(this.iconType, 18, Theme.TEXT_ACTIVE));
        } else {
            setForeground(Theme.TEXT_INACTIVE);
            // Re-instanciar el icono con la tonalidad inactiva apagada
            setIcon(new VectorIcon(this.iconType, 18, Theme.TEXT_INACTIVE));
        }

        // Forzar al Event Dispatch Thread a encolar un ciclo de redibujo del botón
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Aislar y duplicar el contexto gráfico para prevenir efectos colaterales
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // 2. Cargar filtros de suavizado vectorial de alta fidelidad
            Theme.enableHighFidelity(g2d);

            int width = getWidth();
            int height = getHeight();

            // Rama de Renderizado 1: Estado Activo Seleccionado
            if (active) {
                // Rellenar contenedor principal de fondo
                g2d.setColor(Theme.BTN_ACTIVE_BG);
                g2d.fillRoundRect(0, 0, width, height, 12, 12);

                // Dibujar línea de acento vertical sutil en el lateral izquierdo
                g2d.setColor(Theme.ACCENT_BLUE);
                g2d.fillRoundRect(0, 4, 4, height - 8, 4, 4);
            }
            // Rama de Renderizado 2: Estado Inactivo con Cursor del Mouse encima (Hover)
            else if (getModel().isRollover()) {
                g2d.setColor(new Color(255, 255, 255, 10)); // Transparencia sutil de 10/255
                g2d.fillRoundRect(0, 0, width, height, 12, 12);
            }

        } finally {
            // 3. Destruir siempre la copia de contexto temporal
            g2d.dispose();
        }

        // 4. Invocación de la jerarquía de pintado nativa al final para superponer iconos y textos
        super.paintComponent(g);
    }
}
