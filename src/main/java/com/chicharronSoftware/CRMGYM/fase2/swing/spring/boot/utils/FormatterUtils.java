package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * [MEJORA JUNIOR]
 * Clase utilitaria para centralizar los formateadores de fechas y monedas.
 * Al usar esta clase, nos aseguramos de que toda la aplicación utilice el mismo
 * formato ("dd/MM/yyyy" y "es-AR"), y evitamos instanciar múltiples NumberFormat
 * o DateTimeFormatter en diferentes vistas, ahorrando memoria y previniendo errores.
 */
public class FormatterUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale LOCALE_AR = Locale.forLanguageTag("es-AR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(LOCALE_AR);

    // [MEJORA JUNIOR] Formatea la fecha de forma segura (verifica null)
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    // [MEJORA JUNIOR] Formatea un BigDecimal a la moneda local de forma segura
    public static String formatCurrency(BigDecimal amount) {
        return amount != null ? CURRENCY_FORMAT.format(amount) : CURRENCY_FORMAT.format(BigDecimal.ZERO);
    }
}
