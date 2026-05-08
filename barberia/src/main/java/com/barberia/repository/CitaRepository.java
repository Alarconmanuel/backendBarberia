package com.barberia.repository;

import com.barberia.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Consulta nativa 1: citas de un usuario cliente
    @Query(value = "SELECT * FROM cita WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<Cita> findByUsuario(@Param("idUsuario") Long idUsuario);

    // Consulta nativa 2: citas de un barbero en una fecha
    @Query(value = "SELECT * FROM cita WHERE id_barbero = :idBarbero AND fecha = :fecha", nativeQuery = true)
    List<Cita> findByBarberoYFecha(@Param("idBarbero") Long idBarbero, @Param("fecha") LocalDate fecha);

    // Consulta nativa 3: citas activas de un barbero en una fecha (para verificar disponibilidad)
    @Query(value = """
        SELECT * FROM cita
        WHERE id_barbero = :idBarbero
        AND fecha = :fecha
        AND estado NOT IN ('CANCELADA','NO_PRESENTADO')
        """, nativeQuery = true)
    List<Cita> findActivasByBarberoYFecha(@Param("idBarbero") Long idBarbero, @Param("fecha") LocalDate fecha);
}