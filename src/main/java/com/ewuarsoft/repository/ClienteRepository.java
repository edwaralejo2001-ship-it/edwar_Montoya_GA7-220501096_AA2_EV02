package com.ewuarsoft.repository;

import com.ewuarsoft.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumento(String documento);
    boolean existsByDocumento(String documento);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR c.documento LIKE CONCAT('%', :query, '%')")
    List<Cliente> buscarPorNombreODocumento(@Param("query") String query);
}
