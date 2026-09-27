package com.ewuarsoft.model;

public enum MetodoPago {
    EFECTIVO("Efectivo"),
    TRANSFERENCIA_NEQUI("Transferencia Bancaria / Nequi / Dale"),
    TARJETA_DEBITO("Tarjeta Débito"),
    TARJETA_CREDITO("Tarjeta Crédito");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
