package com.barberia.repository;

import com.barberia.model.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BarberoRepository extends JpaRepository<Barbero, Long> {

    // Consulta nativa 1: listar barberos activos
    @Query(value = "SELECT * FROM barbero WHERE activo = true", nativeQuery = true)
    List<Barbero> findActivos();

    // Consulta nativa 2: barberos ordenados por cantidad de citas
    @Query(value = """
        SELECT b.* FROM barbero b
        JOIN cita c ON b.id_barbero = c.id_barbero
        GROUP BY b.id_barbero
        ORDER BY COUNT(c.id_cita) DESC
        """, nativeQuery = true)
    List<Barbero> findMasSolicitados();
}