package com.barberia.service;

import com.barberia.dto.NotificacionDTO;
import java.util.List;

public interface NotificacionService {
    List<NotificacionDTO> findAll();
    NotificacionDTO findById(Long id);
    NotificacionDTO save(NotificacionDTO dto);
    void deleteById(Long id);
    List<NotificacionDTO> findByCita(Long idCita);
}