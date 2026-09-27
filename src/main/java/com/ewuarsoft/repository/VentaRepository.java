package com.ewuarsoft.repository;

import com.ewuarsoft.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    Optional<Venta> findByNumeroFactura(String numeroFactura);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v")
    BigDecimal sumTotalVentas();

    List<Venta> findTop10ByOrderByFechaHoraDesc();
}
