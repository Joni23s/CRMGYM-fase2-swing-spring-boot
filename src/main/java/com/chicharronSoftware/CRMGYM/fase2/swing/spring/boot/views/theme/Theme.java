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

    // Colores de Fondo (Backgrounds)
    public static final Color BG_DARK = new Color(0x090D16);       // Fondo principal ultra oscuro de la aplicación
    public static final Color CARD_BG = new Color(0x1E293B);       // Fondo de tarjetas y paneles contenedores
    public static final Color CARD_BG_ALT = new Color(0x151F32);   // Fondo alternativo/secundario para contraste interno
    public static final Color BTN_ACTIVE_BG = new Color(0x1E293B); // Fondo para botones activos de navegación/sidebar

    // Bordes y Rejillas (Borders & Grids)
    public static final Color BORDER_SLATE = new Color(0x334155);  // Bordes sutiles para delimitar componentes oscuros

    // Jerarquía de Texto (Text Hierarchy)
    public static final Color TEXT_ACTIVE = new Color(0xF8FAFC);    // Texto primario, altamente legible y luminoso
    public static final Color TEXT_INACTIVE = new Color(0x94A3B8);  // Texto secundario o deshabilitado, reduce ruido visual

    // Acentos de Marca (Brand Accents)
    public static final Color ACCENT_CYAN = new Color(0x06B6D4);    // Acento cian brillante para interacción primaria o resaltados
    public static final Color ACCENT_BLUE = new Color(0x3B82F6);    // Azul corporativo para botones estándar y enlaces

    // Estado Semántico (Semantic Status / Badges)
    public static final Color STATUS_SUCCESS = new Color(0x10B981); // Verde esmeralda para operaciones exitosas y activos
    public static final Color STATUS_WARNING = new Color(0xF97316); // Naranja para advertencias o estados pendientes
    public static final Color STATUS_ERROR = new Color(0xEF4444);   // Rojo brillante para errores, alertas y cancelaciones

    /* 
     * =============================================================================
     * 2. TOKENS DE TIPOGRAFÍA (ESTRUCTURA SEGOE UI)
     * =============================================================================
     * Pre-instanciamos las tipografías para evitar re-crear e interpretar los mapas
     * vectoriales de la fuente en tiempo de renderizado, optimizando los ciclos del EDT.
     */
    public static final Font FONT_DASHBOARD_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SECTION_TITLE   = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_CARD_TITLE      = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY            = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MONO            = new Font("Consolas", Font.BOLD, 13); // Ideal para datos financieros/monetarios por su ancho monoespaciado uniforme
    public static final Font FONT_FOOTER          = new Font("Segoe UI", Font.BOLD, 11);

    /* 
     * =============================================================================
     * 3. TOKENS DE GEOMETRÍA Y ESPACIADO
     * =============================================================================
     * Constantes numéricas para garantizar consistencia estructural en la distribución
     * de cajas, paddings, gaps de layouts y curvaturas de esquinas redondeadas.
     */

    // Radios de Esquinas Redondeadas (Arcs)
    public static final int ARC_CARD = 20;     // Suavizado premium para tarjetas de información
    public static final int ARC_BUTTON = 10;   // Curvatura óptima para botones táctiles/interactivos
    public static final int ARC_CAPSULE = 16;  // Curvatura tipo píldora para tags, etiquetas y badges

    // Escala Estricta de Márgenes e Intervalos (Layout Paddings/Gaps)
    public static final int GAP_XS = 4;
    public static final int GAP_SM = 8;
    public static final int GAP_MD = 12;
    public static final int GAP_LG = 16;
    public static final int GAP_XL = 24;
    public static final int GAP_XXL = 32;

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
