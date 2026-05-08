package com.barberia.service;

import com.barberia.dto.PromedioClienteDTO;
import java.util.List;

public interface PromedioClienteService {
    List<PromedioClienteDTO> findAll();
    PromedioClienteDTO findById(Long id);
    PromedioClienteDTO findByUsuarioYBarbero(Long idUsuario, Long idBarbero);
}