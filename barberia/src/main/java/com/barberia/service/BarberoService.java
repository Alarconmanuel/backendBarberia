package com.barberia.service;

import com.barberia.dto.BarberoDTO;
import java.util.List;

public interface BarberoService {
    List<BarberoDTO> findAll();
    BarberoDTO findById(Long id);
    BarberoDTO save(BarberoDTO dto);
    void deleteById(Long id);
    List<BarberoDTO> findActivos();
    List<BarberoDTO> findMasSolicitados();
}