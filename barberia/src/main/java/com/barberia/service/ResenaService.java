package com.barberia.service;

import com.barberia.dto.ResenaDTO;
import java.util.List;

public interface ResenaService {
    List<ResenaDTO> findAll();
    ResenaDTO findById(Long id);
    ResenaDTO save(ResenaDTO dto);
    void deleteById(Long id);
    List<ResenaDTO> findByBarbero(Long idBarbero);
}