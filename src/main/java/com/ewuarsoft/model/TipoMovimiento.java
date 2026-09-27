package com.ewuarsoft.model;

public enum TipoMovimiento {
    ENTRADA("Entrada de Stock"),
    SALIDA("Salida de Stock");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
