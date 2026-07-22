package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * =================================================================================
 * THEME SYSTEM: GESTOR CENTRAL DE TOKENS DE DISEÑO (CRMGYM v2)
 * =================================================================================
 * 
 * ---------------------------------------------------------------------------------
 * 1. TEORÍA DE LOS "DESIGN TOKENS" (TOKENS DE DISEÑO)
 * ---------------------------------------------------------------------------------
 * Los Design Tokens son los átomos visuales de nuestro sistema de diseño. Representan
 * las decisiones de diseño codificadas de manera agnóstica a la plataforma, tales como
 * colores, tipografía, espaciado, elevación y geometrías. Al centralizar estos valores:
 * 
 * - Mantenemos la consistencia visual a lo largo de toda la interfaz del CRM.
 * - Evitamos la dispersión de números y colores mágicos ("magic numbers / colors")
 *   dentro del código de pintado personalizado.
 * - Facilitamos refactorizaciones temáticas futuras (por ejemplo, implementar Light/Dark
 *   Mode dinámico) modificando un único punto de verdad.
 * 
 * ---------------------------------------------------------------------------------
 * 2. OPTIMIZACIÓN EN SWING: EL USO DE 'static final' CONSTANTS
 * ---------------------------------------------------------------------------------
 * En Java Swing, definir los tokens como variables estáticas y constantes (`public static final`)
 * es una práctica de alto rendimiento recomendada por las siguientes razones:
 * 
 * - Evita la instanciación redundante: Al definir variables inmutables compiladas en tiempo
 *   de carga de la clase, eliminamos la necesidad de instanciar repetidamente los mismos
 *   colores o fuentes en cada ciclo de renderizado.
 * - Reducción de la huella de memoria: Se aprovecha al máximo el almacenamiento único en el
 *   "Metaspace" (área de memoria dedicada a metadatos de clases y constantes).
 * 
 * ---------------------------------------------------------------------------------
 * 3. GESTIÓN DE MEMORIA EN LA JVM Y EL EVENT DISPATCH THREAD (EDT)
 * ---------------------------------------------------------------------------------
 * El Event Dispatch Thread (EDT) es el hilo encargado de despachar eventos del sistema y
 * redibujar la interfaz gráfica en Swing. Si durante el método `paintComponent(Graphics g)`
 * instanciamos repetidamente objetos mediante `new Color(...)` o `new Font(...)`, causamos
 * un problema crítico:
 * 
 * - Garbage Collection (GC) Pressure: Millones de micro-objetos de vida corta son creados
 *   durante un simple evento de scroll o redimensionamiento. Esto fuerza la activación del
 *   Garbage Collector de la JVM, provocando micro-pausas (stuttering/lag) en la interfaz gráfica.
 * - Cache de Recursos de la JVM: La JVM almacena y gestiona referencias fijas cuando usamos
 *   nuestras constantes `static final`. Además, a nivel de sistema operativo y Java 2D,
 *   los recursos nativos de rasterización asociados a una instancia de Font o Color única son
 *   almacenados en caché por el pipeline de renderizado nativo (DirectX/OpenGL/D3D). Al
 *   reutilizar siempre la misma referencia de objeto, Java 2D evita regenerar las estructuras
 *   gráficas nativas subyacentes, logrando un pintado ultra-rápido en el EDT.
 */
public final class Theme {

    // Constructor privado para evitar instanciación accidental, ya que es una clase puramente utilitaria.
    private Theme() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria de tokens y no puede ser instanciada.");
    }

    /* 
     * =============================================================================
     * 1. TOKENS DE COLOR (PALETA SLATE PREMIUM)
     * =============================================================================
     * Diseñada para interfaces oscuras premium de baja fatiga visual, alta elegancia y
     * alto contraste accesible según normas WCAG.
     */

    // Colores de Fondo (Backgrounds - Papel Envejecido)
    public static final Color BG_DARK = Color.decode("#EFE3C8");       // Fondo principal papel envejecido
    public static final Color CARD_BG = Color.decode("#F5EDDA");       // Fondo de tarjetas y paneles contenedores
    public static final Color CARD_BG_ALT = Color.decode("#EADFC8");   // Fondo alternativo/secundario
    public static final Color BTN_ACTIVE_BG = Color.decode("#8C2320"); // Fondo para botones activos (Rojo sangre de toro)

    // Bordes y Rejillas (Tinta Oscura)
    public static final Color BORDER_SLATE = Color.decode("#211B15");  // Bordes de tinta recta 1px

    // Jerarquía de Texto (Tinta Oscura & Subtonos)
    public static final Color TEXT_ACTIVE = Color.decode("#211B15");    // Texto primario tinta negra/marrón
    public static final Color TEXT_INACTIVE = Color.decode("#5C4E43");  // Texto secundario

    // Acentos del Sistema Vintage
    public static final Color ACCENT_OXBLOOD = Color.decode("#8C2320");   // Rojo sangre de toro (Primario)
    public static final Color ACCENT_MUSTARD = Color.decode("#C68A1E");   // Mostaza decorativo (Solo líneas/ornamentos)
    public static final Color TEXT_MUSTARD = Color.decode("#8C5E12");     // Mostaza legible para texto
    public static final Color ACCENT_SLATE_BLUE = Color.decode("#2E4057");// Azul apagado (Uso puntual en avatares)

    // Acentos legacy mapeados a la nueva paleta
    public static final Color ACCENT_CYAN = ACCENT_OXBLOOD;
    public static final Color ACCENT_BLUE = ACCENT_SLATE_BLUE;

    // Estado Semántico (Sello de Tinta)
    public static final Color STATUS_SUCCESS = TEXT_ACTIVE;    // Confirmado en Tinta
    public static final Color STATUS_WARNING = TEXT_MUSTARD;   // Pendiente en Mostaza legible
    public static final Color STATUS_ERROR = ACCENT_OXBLOOD;   // Vencido/Error en Sangre de Toro

    /* 
     * =============================================================================
     * 2. TOKENS DE TIPOGRAFÍA (ESTILO EDITORIAL VINTAGE POS)
     * =============================================================================
     */
    public static final Font FONT_DASHBOARD_TITLE = new Font("Bevan", Font.BOLD, 22);
    public static final Font FONT_SECTION_TITLE   = new Font("Oswald", Font.BOLD, 15);
    public static final Font FONT_CARD_TITLE      = new Font("Oswald", Font.BOLD, 13);
    public static final Font FONT_BODY            = new Font("Courier Prime", Font.PLAIN, 13);
    public static final Font FONT_MONO            = new Font("Courier Prime", Font.BOLD, 13);
    public static final Font FONT_FOOTER          = new Font("Oswald", Font.BOLD, 11);

    /* 
     * =============================================================================
     * 3. TOKENS DE GEOMETRÍA Y ESPACIADO (ARC 0 - BORDES RECTOS 1PX)
     * =============================================================================
     */

    // Radios de Esquinas Redondeadas (Arc = 0 para estilo vintage plano)
    public static final int ARC_CARD = 0;
    public static final int ARC_BUTTON = 0;
    public static final int ARC_CAPSULE = 0;

    // Escala Estricta de Márgenes e Intervalos (Layout Paddings/Gaps)
    public static final int GAP_XS = 4;
    public static final int GAP_SM = 8;
    public static final int GAP_MD = 12;
    public static final int GAP_LG = 16;
    public static final int GAP_XL = 24;
    public static final int GAP_XXL = 32;

    // Bordes y radios
    public static final int BORDER_RADIUS_SM = 0;
    public static final int BORDER_RADIUS_MD = 0;
    public static final int BORDER_RADIUS_LG = 0;

    // Márgenes y paddings
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 12;
    public static final int SPACING_LG = 16;

    // Botones específicos
    public static final Color BTN_PRIMARY_BG = ACCENT_OXBLOOD;
    public static final Color BTN_PRIMARY_FG = Color.WHITE;
    public static final Color BTN_SECONDARY_BG = CARD_BG;
    public static final Color BTN_SECONDARY_FG = TEXT_ACTIVE;

    /* 
     * =============================================================================
     * 4. ASISTENTE DE RENDERIZADO DE ALTA FIDELIDAD (HIGH-FIDELITY RENDERING HELPER)
     * =============================================================================
     */

    /**
     * Configura un contexto Graphics2D inyectando un conjunto óptimo de sugerencias
     * de renderizado (Rendering Hints) para lograr curvas perfectas y fuentes nítidas.
     * 
     * @param g2d El contexto gráfico 2D a configurar.
     * 
     * ---------------------------------------------------------------------------------
     * EXPLICACIÓN TÉCNICA DEL SUB-PIXEL LCD RENDERING (VALUE_TEXT_ANTIALIAS_LCD_HRGB)
     * ---------------------------------------------------------------------------------
     * El antialiasing estándar de escala de grises suaviza los bordes de la fuente mezclando
     * el color del texto con el color del fondo. Esto a menudo genera un efecto "borroso" o
     * desenfocado que cansa la vista del usuario en pantallas de escritorio.
     * 
     * El renderizado sub-pixel LCD aprovecha la estructura física de los píxeles de un monitor LCD,
     * los cuales están compuestos por tres sub-píxeles verticales individuales (Rojo, Verde y Azul).
     * En lugar de tratar a un píxel entero como la unidad más pequeña, Java 2D activa
     * selectivamente los sub-píxeles a nivel individual para suavizar los bordes del texto a una
     * resolución horizontal tres veces mayor que la nominal.
     * 
     * Esto proporciona:
     * 1. Una nitidez tipográfica extrema (Sharp Text), similar al renderizado nativo del sistema operativo.
     * 2. Eliminación por completo del bug del difuminado de texto (blurring) al pintar textos en paneles
     *    personalizados usando Swing y Java2D.
     * 3. Legibilidad mejorada en fuentes pequeñas (de 11px a 13px) sobre fondos oscuros.
     */
    public static void enableHighFidelity(Graphics2D g2d) {
        if (g2d == null) {
            return;
        }

        // 1. Antialiasing de Texto Sub-pixel LCD (Layout de subpíxeles horizontales RGB)
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
        );

        // 2. Antialiasing Vectorial Activo (Para bordes de tarjetas, líneas y arcos suaves)
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // 3. Control de Trazado Puro (Desactiva normalizaciones que deforman curvas vectoriales complejas)
        g2d.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL, 
            RenderingHints.VALUE_STROKE_PURE
        );
    }
}
