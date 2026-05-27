package com.barberia.service;

import com.barberia.dto.UsuarioDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Usuario;
import com.barberia.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioServiceIMP implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceIMP.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceIMP(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setCorreo(u.getCorreo());
        dto.setTelefono(u.getTelefono());
        dto.setRol(u.getRol());
        dto.setActivo(u.getActivo());
        return dto;
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u;
        if (dto.getIdUsuario() != null) {
            u = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseGet(Usuario::new);
        } else {
            u = new Usuario();
        }
        u.setIdUsuario(dto.getIdUsuario());
        u.setNombre(dto.getNombre());
        u.setCorreo(dto.getCorreo());
        u.setTelefono(dto.getTelefono());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(dto.getPassword());
        }
        u.setRol(dto.getRol());
        u.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return u;
    }

    @Override
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public UsuarioDTO findById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public UsuarioDTO save(UsuarioDTO dto) {
        log.info("Creando/actualizando usuario: {}", dto.getCorreo());
        return toDTO(usuarioRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando usuario con id: {}", id);
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<UsuarioDTO> findActivosByRol(String rol) {
        return usuarioRepository.findActivosByRol(rol).stream().map(this::toDTO).toList();
    }

    @Override
    public List<UsuarioDTO> findByNombre(String nombre) {
        return usuarioRepository.findByNombreContaining(nombre)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
