package com.ewuarsoft.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false)
    private MotivoMovimiento motivo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    public MovimientoInventario() {
    }

    public MovimientoInventario(Producto producto, TipoMovimiento tipoMovimiento, MotivoMovimiento motivo, Integer cantidad, Integer stockAnterior, Integer stockNuevo, String usuario, String observacion) {
        this.producto = producto;
        this.tipoMovimiento = tipoMovimiento;
        this.motivo = motivo;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.usuario = usuario;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public MotivoMovimiento getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoMovimiento motivo) {
        this.motivo = motivo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
