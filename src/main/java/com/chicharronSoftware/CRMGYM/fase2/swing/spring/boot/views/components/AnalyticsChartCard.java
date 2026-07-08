package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * =================================================================================
 * SITEMA VECTORIAL: ANALYTICS CHART CARD (GRÁFICO DE TENDENCIA DE ALTA FIDELIDAD)
 * =================================================================================
 * 
 * 1. MATEMÁTICAS DE LA CARTOGRAFÍA GRÁFICA (COORDINATE MAPPING):
 *    - La JComponent posee un sistema de coordenadas de pantalla donde el origen (0, 0)
 *      se ubica en la esquina superior izquierda. El eje X crece hacia la derecha y
 *      el eje Y crece hacia abajo.
 *    - Para representar un arreglo numérico de datos (ej. ingresos por mes):
 *      1. Encontramos el valor mínimo (`min`) y el máximo (`max`) del conjunto de datos.
 *      2. Definimos una caja contenedora interna (`chartX`, `chartY`, `chartW`, `chartH`) para el gráfico.
 *      3. El eje X de cada punto `i` se calcula espaciado proporcionalmente:
 *         x_i = chartX + (i * chartW / (points - 1))
 *      4. El eje Y se escala de forma invertida, restando la altura proporcional del piso del gráfico:
 *         y_i = (chartY + chartH) - ((val_i - min) * chartH / range)
 * 
 * 2. CURVATURA SUAVE (BEZIER/QUADRATIC SPLINES) VS LÍNEAS RÍGIDAS:
 *    - Unir puntos con líneas rectas simples produce un gráfico de sierra (zig-zag) de aspecto tosco.
 *    - Para lograr líneas continuas y fluidas de apariencia "líquida", utilizamos `Path2D.Float`
 *      junto a curvas cuadráticas (`quadTo`). Calculamos los puntos medios ("midpoints")
 *      entre cada par de coordenadas sucesivas y los usamos como extremos de la curva cuadrática,
 *      empleando el punto de datos real como el polo de control de curvatura.
 * 
 * 3. CONTROL DE RENDIMIENTO Y RENDERING HINTS:
 *    - Evitamos instanciar pinceles o gradientes en cada frame para no sobrecargar el EDT.
 *    - Habilitamos el filtrado vectorial `Theme.enableHighFidelity()` para que las líneas
 *      curvas no presenten dientes de sierra ("aliasing").
 * =================================================================================
 */
public class AnalyticsChartCard extends JPanel {

    private final String chartTitle;
    private final int[] dataValues;
    private final String[] xAxisLabels;

    /**
     * Constructor principal del componente de gráfico analítico.
     * 
     * @param title  Título del panel analítico (ej. "Ingresos Mensuales").
     * @param values Conjunto numérico de puntos a graficar.
     * @param labels Etiquetas para el eje X de cada punto.
     */
    public AnalyticsChartCard(String title, int[] values, String[] labels) {
        this.chartTitle = title;
        this.dataValues = values != null ? values : new int[0];
        this.xAxisLabels = labels != null ? labels : new String[0];

        // Configuración visual base
        setOpaque(false);
        setLayout(new MigLayout("wrap 1, ins 18 22 18 22, fill", "[grow, fill]", "[]15[grow, fill]"));

        // Cabecera de la tarjeta
        JLabel lblTitle = new JLabel(this.chartTitle);
        lblTitle.setFont(Theme.FONT_SECTION_TITLE);
        lblTitle.setForeground(Theme.TEXT_ACTIVE);
        add(lblTitle, "alignx left");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Crear contexto gráfico aislado
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // 2. Activar suavizado tipográfico y de trazados
            Theme.enableHighFidelity(g2d);

            int width = getWidth();
            int height = getHeight();

            // 3. Pintar fondo y borde de la tarjeta contenedora
            g2d.setColor(Theme.CARD_BG);
            g2d.fillRoundRect(0, 0, width, height, Theme.ARC_CARD, Theme.ARC_CARD);

            g2d.setColor(Theme.BORDER_SLATE);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(0, 0, width - 1, height - 1, Theme.ARC_CARD, Theme.ARC_CARD);

            // Resguardo por si el arreglo está vacío
            if (dataValues.length < 2) {
                drawEmptyState(g2d, width, height);
                return;
            }

            // 4. Configurar dimensiones de la caja del gráfico analítico
            int chartX = 40;
            int chartY = 50;
            int chartW = width - 80;
            int chartH = height - 100;

            // --- 5. Dibujar Rejilla de Fondo (Grid Axis) ---
            g2d.setColor(new Color(51, 65, 85, 50)); // Slate 700 con 20% opacidad
            g2d.setStroke(new BasicStroke(1.0f));
            int gridLines = 4;
            for (int i = 0; i < gridLines; i++) {
                int gridY = chartY + (i * chartH) / (gridLines - 1);
                g2d.drawLine(chartX, gridY, chartX + chartW, gridY);
            }

            // --- 6. Normalización de Datos y Mapeo a Píxeles ---
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int val : dataValues) {
                if (val < min) min = val;
                if (val > max) max = val;
            }
            int range = max - min;
            if (range == 0) {
                range = 1; // Prevenir división por cero
            }

            int pointsCount = dataValues.length;
            float[] x = new float[pointsCount];
            float[] y = new float[pointsCount];

            for (int i = 0; i < pointsCount; i++) {
                x[i] = chartX + ((float) i * chartW) / (pointsCount - 1);
                y[i] = (chartY + chartH) - ((float) (dataValues[i] - min) * chartH) / range;
            }

            // --- 7. Construcción de Camino Suavizado (Path Vectorial) ---
            Path2D.Float curvePath = new Path2D.Float();
            curvePath.moveTo(x[0], y[0]);

            // Spline cuadrático por puntos medios para un suavizado fluido sin esquinas toscas
            for (int i = 0; i < pointsCount - 1; i++) {
                float midX = (x[i] + x[i + 1]) / 2.0f;
                float midY = (y[i] + y[i + 1]) / 2.0f;
                if (i == 0) {
                    curvePath.lineTo(midX, midY);
                } else {
                    curvePath.quadTo(x[i], y[i], midX, midY);
                }
            }
            curvePath.lineTo(x[pointsCount - 1], y[pointsCount - 1]);

            // --- 8. Capa 1: Relleno de Área Translúcida (Gradiente de Brillo) ---
            Path2D.Float glowArea = new Path2D.Float(curvePath);
            glowArea.lineTo(x[pointsCount - 1], chartY + chartH); // Esquina inferior derecha
            glowArea.lineTo(x[0], chartY + chartH);              // Esquina inferior izquierda
            glowArea.closePath();

            // Gradiente vertical: de cian translúcido arriba a totalmente transparente en el piso del gráfico
            GradientPaint areaGradient = new GradientPaint(
                    0, chartY, new Color(6, 182, 212, 45), // Cyan con 18% opacidad
                    0, chartY + chartH, new Color(6, 182, 212, 0)
            );
            g2d.setPaint(areaGradient);
            g2d.fill(glowArea);

            // --- 9. Capa 2: Dibujar la Línea de Tendencia Principal (Trazado de curva) ---
            // Gradiente horizontal: desplaza de azul corporativo a cian brillante a lo largo de la curva
            GradientPaint lineGradient = new GradientPaint(
                    chartX, 0, Theme.ACCENT_BLUE,
                    chartX + chartW, 0, Theme.ACCENT_CYAN
            );
            g2d.setPaint(lineGradient);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.draw(curvePath);

            // --- 10. Capa 3: Dibujar Etiquetas de Eje X (X-Axis Labels) ---
            g2d.setColor(Theme.TEXT_INACTIVE);
            g2d.setFont(Theme.FONT_FOOTER);
            FontMetrics fm = g2d.getFontMetrics(Theme.FONT_FOOTER);

            for (int i = 0; i < pointsCount; i++) {
                if (i < xAxisLabels.length) {
                    String label = xAxisLabels[i];
                    int labelW = fm.stringWidth(label);
                    // Centrar horizontalmente la etiqueta respecto a la coordenada X de la columna del punto
                    int labelX = (int) (x[i] - (labelW / 2.0f));
                    int labelY = chartY + chartH + 22; // 22 píxeles de espacio debajo del piso del gráfico
                    g2d.drawString(label, labelX, labelY);
                }
            }

        } finally {
            g2d.dispose();
        }
    }

    /**
     * Dibuja un estado de contingencia si no hay suficientes datos cargados para renderizar el gráfico.
     */
    private void drawEmptyState(Graphics2D g2d, int w, int h) {
        g2d.setColor(Theme.TEXT_INACTIVE);
        g2d.setFont(Theme.FONT_BODY);
        String msg = "No hay suficientes datos para generar el gráfico.";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
    }
}
