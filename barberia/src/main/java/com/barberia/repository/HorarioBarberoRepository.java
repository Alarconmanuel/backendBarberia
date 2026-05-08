package com.barberia.repository;

import com.barberia.model.HorarioBarbero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HorarioBarberoRepository extends JpaRepository<HorarioBarbero, Long> {

    // Consulta nativa 1: horarios de un barbero
    @Query(value = "SELECT * FROM horario_barbero WHERE id_barbero = :idBarbero", nativeQuery = true)
    List<HorarioBarbero> findByBarbero(@Param("idBarbero") Long idBarbero);

    // Consulta nativa 2: horario de un barbero en un dia especifico
    @Query(value = "SELECT * FROM horario_barbero WHERE id_barbero = :idBarbero AND dia_semana = :dia", nativeQuery = true)
    List<HorarioBarbero> findByBarberoYDia(@Param("idBarbero") Long idBarbero, @Param("dia") String dia);
}