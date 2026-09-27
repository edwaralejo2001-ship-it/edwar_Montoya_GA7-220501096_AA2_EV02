package com.ewuarsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El documento o cédula es obligatorio")
    @Size(min = 4, max = 30, message = "El documento debe tener entre 4 y 30 caracteres")
    @Column(name = "documento", nullable = false, unique = true, length = 30)
    private String documento;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "direccion", length = 200)
    private String direccion;

    public Cliente() {
    }

    public Cliente(String documento, String nombre, String telefono, String email, String direccion) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
