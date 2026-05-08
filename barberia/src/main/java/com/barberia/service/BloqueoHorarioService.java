package com.barberia.service;

import com.barberia.dto.BloqueoHorarioDTO;
import java.util.List;

public interface BloqueoHorarioService {
    List<BloqueoHorarioDTO> findAll();
    BloqueoHorarioDTO findById(Long id);
    BloqueoHorarioDTO save(BloqueoHorarioDTO dto);
    void deleteById(Long id);
    List<BloqueoHorarioDTO> findByBarbero(Long idBarbero);
}