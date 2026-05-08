package com.barberia.service;

import com.barberia.dto.ResenaDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Resena;
import com.barberia.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ResenaServiceIMP implements ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaServiceIMP.class);

    private final ResenaRepository resenaRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BarberoRepository barberoRepository;

    public ResenaServiceIMP(ResenaRepository resenaRepository, CitaRepository citaRepository,
                            UsuarioRepository usuarioRepository, BarberoRepository barberoRepository) {
        this.resenaRepository = resenaRepository;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.barberoRepository = barberoRepository;
    }

    private ResenaDTO toDTO(Resena r) {
        ResenaDTO dto = new ResenaDTO();
        dto.setIdResena(r.getIdResena());
        dto.setIdCita(r.getCita() != null ? r.getCita().getIdCita() : null);
        dto.setIdUsuario(r.getUsuario() != null ? r.getUsuario().getIdUsuario() : null);
        dto.setIdBarbero(r.getBarbero() != null ? r.getBarbero().getIdBarbero() : null);
        dto.setCalificacion(r.getCalificacion());
        dto.setComentario(r.getComentario());
        return dto;
    }

    private Resena toEntity(ResenaDTO dto) {
        Resena r = new Resena();
        r.setIdResena(dto.getIdResena());
        r.setCalificacion(dto.getCalificacion());
        r.setComentario(dto.getComentario());
        if (dto.getIdCita() != null)
            r.setCita(citaRepository.findById(dto.getIdCita())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + dto.getIdCita())));
        if (dto.getIdUsuario() != null)
            r.setUsuario(usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getIdUsuario())));
        if (dto.getIdBarbero() != null)
            r.setBarbero(barberoRepository.findById(dto.getIdBarbero())
                    .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + dto.getIdBarbero())));
        return r;
    }

    @Override
    public List<ResenaDTO> findAll() {
        return resenaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public ResenaDTO findById(Long id) {
        return resenaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con id: " + id));
    }

    @Override
    @Transactional
    public ResenaDTO save(ResenaDTO dto) {
        log.info("Creando resena para cita {}, barbero {}, calificacion {}", dto.getIdCita(), dto.getIdBarbero(), dto.getCalificacion());
        return toDTO(resenaRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando resena con id: {}", id);
        resenaRepository.deleteById(id);
    }

    @Override
    public List<ResenaDTO> findByBarbero(Long idBarbero) {
        return resenaRepository.findByBarbero(idBarbero).stream().map(this::toDTO).toList();
    }
}
