package com.barberia.repository;

import com.barberia.model.PromedioCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PromedioClienteRepository extends JpaRepository<PromedioCliente, Long> {

    // Consulta nativa 1: promedio de un cliente con un barbero especifico
    @Query(value = "SELECT * FROM promedio_cliente WHERE id_usuario = :idUsuario AND id_barbero = :idBarbero", nativeQuery = true)
    Optional<PromedioCliente> findByUsuarioYBarbero(@Param("idUsuario") Long idUsuario,
                                                    @Param("idBarbero") Long idBarbero);
}