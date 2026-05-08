package com.barberia.repository;

import com.barberia.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // Consulta nativa 1: resenas de un barbero
    @Query(value = "SELECT * FROM resena WHERE id_barbero = :idBarbero", nativeQuery = true)
    List<Resena> findByBarbero(@Param("idBarbero") Long idBarbero);

    // Consulta nativa 2: verificar si ya existe resena para una cita
    @Query(value = "SELECT * FROM resena WHERE id_cita = :idCita", nativeQuery = true)
    Optional<Resena> findByCita(@Param("idCita") Long idCita);
}