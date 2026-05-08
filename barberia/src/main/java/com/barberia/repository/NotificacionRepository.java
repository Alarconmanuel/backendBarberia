package com.barberia.repository;

import com.barberia.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Consulta nativa 1: notificaciones de una cita
    @Query(value = "SELECT * FROM notificacion WHERE id_cita = :idCita", nativeQuery = true)
    List<Notificacion> findByCita(@Param("idCita") Long idCita);
}