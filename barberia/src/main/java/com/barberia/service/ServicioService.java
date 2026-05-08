package com.barberia.service;

import com.barberia.dto.ServicioDTO;
import java.util.List;

public interface ServicioService {
    List<ServicioDTO> findAll();
    ServicioDTO findById(Long id);
    ServicioDTO save(ServicioDTO dto);
    void deleteById(Long id);
    List<ServicioDTO> findByDuracionMenorIgual(Integer minutos);
}