package com.barberia.repository;

import com.barberia.model.BloqueoHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloqueoHorarioRepository extends JpaRepository<BloqueoHorario, Long> {

    // Consulta nativa 1: bloqueos de un barbero
    @Query(value = "SELECT * FROM bloqueo_horario WHERE id_barbero = :idBarbero", nativeQuery = true)
    List<BloqueoHorario> findByBarbero(@Param("idBarbero") Long idBarbero);

    // Consulta nativa 2: verificar bloqueo activo en un rango de tiempo
    @Query(value = """
        SELECT * FROM bloqueo_horario
        WHERE id_barbero = :idBarbero
        AND fecha_inicio < :fin
        AND fecha_fin > :inicio
        """, nativeQuery = true)
    List<BloqueoHorario> findBloqueoEnRango(@Param("idBarbero") Long idBarbero,
                                            @Param("inicio") LocalDateTime inicio,
                                            @Param("fin") LocalDateTime fin);
}