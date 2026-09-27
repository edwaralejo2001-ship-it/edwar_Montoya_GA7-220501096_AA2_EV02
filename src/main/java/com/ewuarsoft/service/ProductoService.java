package com.ewuarsoft.service;

import com.ewuarsoft.model.MotivoMovimiento;
import com.ewuarsoft.model.MovimientoInventario;
import com.ewuarsoft.model.Producto;
import com.ewuarsoft.model.TipoMovimiento;
import com.ewuarsoft.repository.MovimientoInventarioRepository;
import com.ewuarsoft.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public ProductoService(ProductoRepository productoRepository, MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public List<Producto> buscar(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productoRepository.findAll();
        }
        return productoRepository.buscarPorNombreOCodigo(query.trim());
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> listarProductosBajoStock() {
        return productoRepository.findProductosConBajoStock();
    }

    public long contarProductosBajoStock() {
        return productoRepository.countProductosConBajoStock();
    }

    public long contarProductos() {
        return productoRepository.count();
    }

    public BigDecimal calcularValorTotalInventario() {
        return productoRepository.calcularValorTotalInventario();
    }

    @Transactional
    public Producto guardar(Producto producto) {
        if (producto.getId() == null && productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el código " + producto.getCodigo());
        }
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    @Transactional
    public MovimientoInventario registrarMovimiento(Long productoId, TipoMovimiento tipo, MotivoMovimiento motivo, Integer cantidad, String usuario, String observacion) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

        int stockAnterior = producto.getStockActual();
        int stockNuevo;

        if (tipo == TipoMovimiento.ENTRADA) {
            stockNuevo = stockAnterior + cantidad;
        } else {
            if (stockAnterior < cantidad) {
                throw new IllegalArgumentException("No hay suficiente stock disponible. Stock actual: " + stockAnterior + ", Cantidad requerida: " + cantidad);
            }
            stockNuevo = stockAnterior - cantidad;
        }

        producto.setStockActual(stockNuevo);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario(
                producto,
                tipo,
                motivo,
                cantidad,
                stockAnterior,
                stockNuevo,
                usuario != null ? usuario : "Sistema",
                observacion
        );

        return movimientoRepository.save(movimiento);
    }
}
