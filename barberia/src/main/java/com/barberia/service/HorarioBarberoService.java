package com.barberia.service;

import com.barberia.dto.HorarioBarberoDTO;
import java.util.List;

public interface HorarioBarberoService {
    List<HorarioBarberoDTO> findAll();
    HorarioBarberoDTO findById(Long id);
    HorarioBarberoDTO save(HorarioBarberoDTO dto);
    void deleteById(Long id);
    List<HorarioBarberoDTO> findByBarbero(Long idBarbero);
    List<HorarioBarberoDTO> findByBarberoYDia(Long idBarbero, String dia);
}