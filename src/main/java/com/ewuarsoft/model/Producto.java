package com.ewuarsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código o SKU es obligatorio")
    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotNull(message = "Debe seleccionar una categoría")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de compra no puede ser negativo")
    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de venta no puede ser negativo")
    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser menor a 0")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser menor a 0")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida = "Unidad";

    public Producto() {
    }

    public Producto(String codigo, String nombre, Categoria categoria, BigDecimal precioCompra, BigDecimal precioVenta, Integer stockActual, Integer stockMinimo, String unidadMedida) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
    }

    public boolean isAgotado() {
        return stockActual != null && stockActual <= 0;
    }

    public boolean isStockBajo() {
        return stockActual != null && stockMinimo != null && stockActual > 0 && stockActual <= stockMinimo;
    }

    public String getEstadoStock() {
        if (isAgotado()) {
            return "AGOTADO";
        } else if (isStockBajo()) {
            return "BAJO";
        } else {
            return "NORMAL";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
