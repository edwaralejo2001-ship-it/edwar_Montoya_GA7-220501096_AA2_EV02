package com.ewuarsoft.model;

public enum EstadoUsuario {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String etiqueta;

    EstadoUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
