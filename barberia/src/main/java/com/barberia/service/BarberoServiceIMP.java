package com.barberia.service;

import com.barberia.dto.BarberoDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Barbero;
import com.barberia.model.Usuario;
import com.barberia.repository.BarberoRepository;
import com.barberia.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BarberoServiceIMP implements BarberoService {

    private static final Logger log = LoggerFactory.getLogger(BarberoServiceIMP.class);

    private final BarberoRepository barberoRepository;
    private final UsuarioRepository usuarioRepository;

    public BarberoServiceIMP(BarberoRepository barberoRepository, UsuarioRepository usuarioRepository) {
        this.barberoRepository = barberoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private BarberoDTO toDTO(Barbero b) {
        BarberoDTO dto = new BarberoDTO();
        dto.setIdBarbero(b.getIdBarbero());
        dto.setNombre(b.getNombre());
        dto.setEspecialidad(b.getEspecialidad());
        dto.setTelefono(b.getTelefono());
        dto.setActivo(b.getActivo());
        dto.setIdUsuario(b.getUsuario() != null ? b.getUsuario().getIdUsuario() : null);
        return dto;
    }

    private Barbero toEntity(BarberoDTO dto) {
        Barbero b = new Barbero();
        b.setIdBarbero(dto.getIdBarbero());
        b.setNombre(dto.getNombre());
        b.setEspecialidad(dto.getEspecialidad());
        b.setTelefono(dto.getTelefono());
        b.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getIdUsuario()));
            b.setUsuario(u);
        }
        return b;
    }

    @Override
    public List<BarberoDTO> findAll() {
        return barberoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public BarberoDTO findById(Long id) {
        return barberoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public BarberoDTO save(BarberoDTO dto) {
        log.info("Creando/actualizando barbero: {}", dto.getNombre());
        return toDTO(barberoRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando barbero con id: {}", id);
        barberoRepository.deleteById(id);
    }

    @Override
    public List<BarberoDTO> findActivos() {
        return barberoRepository.findActivos().stream().map(this::toDTO).toList();
    }

    @Override
    public List<BarberoDTO> findMasSolicitados() {
        return barberoRepository.findMasSolicitados().stream().map(this::toDTO).toList();
    }
}
