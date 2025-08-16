package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums;

public enum PaymentMethod {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA("Transferencia"),
    TARJETA_CREDITO("Tarjeta de Credito"),
    TARJETA_DEBITO("Tarjeta de Debito"),
    MERCADO_PAGO("Mercado Pago"),    // opcional: muy usado en gimnasios en LATAM
    PAYPAL("Paypal");       // opcional: si algún día se abre online

    private final String descripcion;

    PaymentMethod(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
