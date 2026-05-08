package com.barberia.service;

import com.barberia.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> findAll();
    UsuarioDTO findById(Long id);
    UsuarioDTO save(UsuarioDTO dto);
    void deleteById(Long id);
    List<UsuarioDTO> findActivosByRol(String rol);
    List<UsuarioDTO> findByNombre(String nombre);  // ← nuevo
}