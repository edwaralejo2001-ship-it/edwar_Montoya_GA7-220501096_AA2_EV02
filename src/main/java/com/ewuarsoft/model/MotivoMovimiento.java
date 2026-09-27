package com.ewuarsoft.model;

public enum MotivoMovimiento {
    COMPRA("Compra a Proveedor"),
    VENTA("Venta a Cliente"),
    MERMA_DANO("Merma / Rotura / Daño"),
    AJUSTE("Ajuste de Conteo Físico"),
    DEVOLUCION("Devolución");

    private final String etiqueta;

    MotivoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
