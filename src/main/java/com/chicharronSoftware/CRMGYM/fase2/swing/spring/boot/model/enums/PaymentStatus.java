package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums;

public enum PaymentStatus {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    VENCIDO("Vencido"),
    PAGADO_VENCIDO("Pagado con atraso"),
    CANCELADO("Cancelado");

    private final String descripcion;

    PaymentStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

