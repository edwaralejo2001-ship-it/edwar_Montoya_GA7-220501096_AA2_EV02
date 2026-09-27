package com.ewuarsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El NIT o identificación es obligatorio")
    @Column(name = "nit", nullable = false, unique = true, length = 30)
    private String nit;

    @NotBlank(message = "La razón social es obligatoria")
    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "contacto", length = 100)
    private String contacto;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    @Column(name = "telefono", nullable = false, length = 30)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "categoria_suministro", length = 100)
    private String categoriaSuministro;

    public Proveedor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
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

    public String getCategoriaSuministro() {
        return categoriaSuministro;
    }

    public void setCategoriaSuministro(String categoriaSuministro) {
        this.categoriaSuministro = categoriaSuministro;
    }
}
