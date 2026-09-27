package com.ewuarsoft.service;

import com.ewuarsoft.model.MovimientoInventario;
import com.ewuarsoft.model.Producto;
import com.ewuarsoft.repository.ClienteRepository;
import com.ewuarsoft.repository.MovimientoInventarioRepository;
import com.ewuarsoft.repository.ProductoRepository;
import com.ewuarsoft.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public DashboardService(ProductoRepository productoRepository,
                            ClienteRepository clienteRepository,
                            UsuarioRepository usuarioRepository,
                            MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        long totalProductos = productoRepository.count();
        long totalClientes = clienteRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long stockCritico = productoRepository.countProductosConBajoStock();
        BigDecimal valorInventario = productoRepository.calcularValorTotalInventario();
        List<Producto> productosBajoStock = productoRepository.findProductosConBajoStock();
        List<MovimientoInventario> ultimosMovimientos = movimientoRepository.findTop15ByOrderByFechaHoraDesc();

        stats.put("totalProductos", totalProductos);
        stats.put("totalClientes", totalClientes);
        stats.put("totalUsuarios", totalUsuarios);
        stats.put("stockCritico", stockCritico);
        stats.put("valorInventario", valorInventario);
        stats.put("productosBajoStock", productosBajoStock);
        stats.put("ultimosMovimientos", ultimosMovimientos);

        return stats;
    }
}
