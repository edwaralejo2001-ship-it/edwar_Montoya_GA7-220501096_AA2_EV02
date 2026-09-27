package com.ewuarsoft.repository;

import com.ewuarsoft.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo ORDER BY p.stockActual ASC")
    List<Producto> findProductosConBajoStock();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    long countProductosConBajoStock();

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> buscarPorNombreOCodigo(@Param("query") String query);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId")
    List<Producto> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("SELECT COALESCE(SUM(p.precioVenta * p.stockActual), 0) FROM Producto p")
    BigDecimal calcularValorTotalInventario();
}
