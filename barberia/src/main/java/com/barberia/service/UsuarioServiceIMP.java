package com.barberia.service;

import com.barberia.dto.UsuarioDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Usuario;
import com.barberia.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioServiceIMP implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceIMP.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceIMP(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        Usuario u = new Usuario();
        u.setIdUsuario(dto.getIdUsuario());
        u.setNombre(dto.getNombre());
        u.setCorreo(dto.getCorreo());
        u.setTelefono(dto.getTelefono());
        u.setPassword(dto.getPassword());
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

    private boolean isBcrypt(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    @Override
    @Transactional
    public UsuarioDTO save(UsuarioDTO dto) {
        log.info("Creando/actualizando usuario: {}", dto.getCorreo());

        // Si es actualización y la contraseña está vacía, conservar la existente
        if (dto.getIdUsuario() != null && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            usuarioRepository.findById(dto.getIdUsuario()).ifPresent(existing -> dto.setPassword(existing.getPassword()));
        }

        Usuario entity = toEntity(dto);

        // Encodear solo si es texto plano (no es BCrypt)
        if (entity.getPassword() != null && !isBcrypt(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }

        return toDTO(usuarioRepository.save(entity));
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
