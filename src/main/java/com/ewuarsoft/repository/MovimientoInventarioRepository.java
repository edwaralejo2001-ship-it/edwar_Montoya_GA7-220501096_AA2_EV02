package com.ewuarsoft.repository;

import com.ewuarsoft.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findTop15ByOrderByFechaHoraDesc();
    List<MovimientoInventario> findByProductoIdOrderByFechaHoraDesc(Long productoId);
}
