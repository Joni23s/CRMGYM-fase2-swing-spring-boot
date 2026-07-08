package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * =================================================================================
 * STATUS BADGE SYSTEM: COMPONENTE CÁPSULA SEMÁNTICA REUTILIZABLE
 * =================================================================================
 * 
 * ---------------------------------------------------------------------------------
 * 1. MECÁNICA DE FontMetrics Y GEOMETRÍA DE TIPOGRAFÍAS EN PANTALLA
 * ---------------------------------------------------------------------------------
 * La clase `FontMetrics` actúa como el puente de medición entre los vectores de una fuente y
 * los píxeles reales rasterizados en la pantalla. Medir la tipografía de forma imprecisa
 * causa recortes ("clipping") o desbordamientos del texto. Sus métricas principales son:
 * 
 * - `stringWidth(String str)`: Retorna el ancho total acumulado de una cadena de caracteres
 *   en píxeles, considerando el interlineado y el grosor del glifo específico bajo la fuente activa.
 * - `getHeight()`: La altura total de una línea de texto de la fuente, calculada sumando
 *   Ascent, Descent y Leading.
 * - `getAscent()`: Distancia en píxeles desde la línea base ("baseline") hacia la parte superior
 *   de los glifos más altos (ej. la letra 'h' o 'T').
 * - `getDescent()`: Distancia en píxeles que los glifos se extienden por debajo de la línea base
 *   (ej. la letra 'g' o 'p').
 * 
 * El uso correcto de `FontMetrics` nos permite calcular dinámicamente los bordes ("bounds")
 * y el tamaño óptimo de las cápsulas basándonos en la longitud variable de los textos
 * sin recurrir a anchos fijos ("hardcoded widths") propensos a fallar ante cambios de idioma.
 * 
 * ---------------------------------------------------------------------------------
 * 2. EL CANAL ALFA EN LA REPRESENTACIÓN DE COLORES SOBRE FONDOS OSCUROS
 * ---------------------------------------------------------------------------------
 * En la interfaz premium Slate de CRMGYM v2, el uso de colores puros y saturados para fondos
 * de etiquetas produce fatiga visual. Para lograr el acabado estético de vidrio translúcido:
 * 
 * - El canal Alfa (Alpha channel) define el nivel de transparencia de un objeto `Color`
 *   (rango de 0 para transparente a 255 para totalmente opaco).
 * - Definimos los fondos con una opacidad del 10% al 12% (un valor alfa de ~25 a 30).
 * - Al mezclar esta capa translúcida sobre el fondo oscuro (`Theme.CARD_BG`), la JVM realiza una
 *   composición alfa en tiempo real. Esto permite que el tono oscuro subyacente se filtre a través
 *   de la cápsula, logrando un matiz sutil e integrado que exuda calidad y legibilidad de alto contraste.
 */
public class StatusBadge extends JLabel {

    public enum BadgeType {
        SUCCESS,
        WARNING,
        ERROR,
        NEUTRAL
    }

    private BadgeType currentType;
    private Color badgeBg;
    private Color badgeBorder;

    /**
     * Constructor principal del StatusBadge.
     * 
     * @param text Texto a mostrar en la cápsula.
     * @param type Tipo semántico inicial.
     */
    public StatusBadge(String text, BadgeType type) {
        super(text);
        
        // 1. Configurar tipografía consistente y alineamiento centralizado
        setFont(Theme.FONT_FOOTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        
        // 2. Apagar la opacidad por defecto para evitar el pintado rectangular gris nativo de JLabel
        setOpaque(false);

        // 3. Resolver estilos visuales del badge
        updateType(text, type);
    }

    /**
     * Actualiza dinámicamente el texto y el tipo semántico del badge en tiempo de ejecución.
     * 
     * @param text Nuevo texto a asignar.
     * @param type Tipo de estilo a aplicar.
     */
    public void updateType(String text, BadgeType type) {
        setText(text);
        this.currentType = type;

        // Mapeo semántico de colores del Design System
        switch (type) {
            case SUCCESS:
                setForeground(Theme.STATUS_SUCCESS);
                // 12% de opacidad para el fondo verde (255 * 0.12 ≈ 30)
                this.badgeBg = new Color(Theme.STATUS_SUCCESS.getRed(), Theme.STATUS_SUCCESS.getGreen(), Theme.STATUS_SUCCESS.getBlue(), 30);
                this.badgeBorder = Theme.STATUS_SUCCESS;
                break;
                
            case WARNING:
                setForeground(Theme.STATUS_WARNING);
                // 12% de opacidad para el fondo naranja (255 * 0.12 ≈ 30)
                this.badgeBg = new Color(Theme.STATUS_WARNING.getRed(), Theme.STATUS_WARNING.getGreen(), Theme.STATUS_WARNING.getBlue(), 30);
                this.badgeBorder = Theme.STATUS_WARNING;
                break;
                
            case ERROR:
                setForeground(Theme.STATUS_ERROR);
                // 12% de opacidad para el fondo rojo (255 * 0.12 ≈ 30)
                this.badgeBg = new Color(Theme.STATUS_ERROR.getRed(), Theme.STATUS_ERROR.getGreen(), Theme.STATUS_ERROR.getBlue(), 30);
                this.badgeBorder = Theme.STATUS_ERROR;
                break;
                
            case NEUTRAL:
                setForeground(Theme.TEXT_INACTIVE);
                // 10% de opacidad para el fondo blanco (255 * 0.10 ≈ 25)
                this.badgeBg = new Color(255, 255, 255, 25);
                this.badgeBorder = Theme.BORDER_SLATE;
                break;
        }

        // Forzar ciclo de repintado del componente en el EDT
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // Obtenemos las métricas de la fuente activa
        FontMetrics fm = getFontMetrics(getFont());
        int textW = fm.stringWidth(getText() != null ? getText() : "");
        
        // El ancho de la cápsula requiere el texto más un padding horizontal (10px a cada lado)
        int badgeW = textW + 20;
        
        // Retornamos el tamaño preferido garantizando espacio vertical suficiente
        return new Dimension(badgeW, 24);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Aislar y duplicar el contexto gráfico para prevenir colisiones de renderizado
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // 2. Activar suavizado tipográfico y vectorial de alta calidad
            Theme.enableHighFidelity(g2d);

            // 3. Medición geométrica precisa usando FontMetrics
            FontMetrics fm = g2d.getFontMetrics(getFont());
            int textW = fm.stringWidth(getText() != null ? getText() : "");
            
            int badgeW = textW + 20;
            int badgeH = 20;

            // 4. Calcular el punto (x, y) de anclaje para centrar perfectamente la cápsula
            // dentro de los límites reales asignados al componente por el Layout Manager
            int x = (getWidth() - badgeW) / 2;
            int y = (getHeight() - badgeH) / 2;

            // 5. Dibujar fondo de cápsula con transparencia (Alpha)
            g2d.setColor(badgeBg);
            g2d.fillRoundRect(x, y, badgeW, badgeH, Theme.ARC_CAPSULE, Theme.ARC_CAPSULE);

            // 6. Dibujar contorno de borde redondeado fino
            g2d.setColor(badgeBorder);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(x, y, badgeW - 1, badgeH - 1, Theme.ARC_CAPSULE, Theme.ARC_CAPSULE);

        } finally {
            // 7. Liberar recursos gráficos nativos asociados a la copia
            g2d.dispose();
        }

        // 8. Pintar el texto del JLabel por encima de nuestra cápsula en z-order superior
        super.paintComponent(g);
    }
}
