package com.barberia.repository;

import com.barberia.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Consulta nativa 1: servicios con duracion menor o igual a X minutos
    @Query(value = "SELECT * FROM servicio WHERE duracion_minutos <= :minutos", nativeQuery = true)
    List<Servicio> findByDuracionMenorIgual(@Param("minutos") Integer minutos);
}