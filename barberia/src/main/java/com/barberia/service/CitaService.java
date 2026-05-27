package com.barberia.service;

import com.barberia.dto.CitaDTO;
import java.time.LocalDate;
import java.util.List;

public interface CitaService {
    List<CitaDTO> findAll();
    CitaDTO findById(Long id);
    CitaDTO save(CitaDTO dto);
    void deleteById(Long id);
    List<CitaDTO> findByUsuario(Long idUsuario);
    List<CitaDTO> findByBarberoYFecha(Long idBarbero, LocalDate fecha);
    CitaDTO cancelar(Long id);
    CitaDTO iniciar(Long id);
    CitaDTO finalizar(Long id);
    CitaDTO marcarNoPresento(Long id);
    List<String> getDisponibilidad(Long idBarbero, LocalDate fecha);
}