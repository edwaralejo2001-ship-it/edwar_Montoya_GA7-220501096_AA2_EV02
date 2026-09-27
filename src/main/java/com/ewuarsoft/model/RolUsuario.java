package com.ewuarsoft.model;

public enum RolUsuario {
    ADMINISTRADOR("Administrador"),
    CAJERO("Cajero"),
    ALMACENISTA("Almacenista");

    private final String etiqueta;

    RolUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
