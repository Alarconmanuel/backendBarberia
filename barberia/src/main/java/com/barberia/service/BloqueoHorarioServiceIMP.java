package com.barberia.service;

import com.barberia.dto.BloqueoHorarioDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.BloqueoHorario;
import com.barberia.repository.BarberoRepository;
import com.barberia.repository.BloqueoHorarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BloqueoHorarioServiceIMP implements BloqueoHorarioService {

    private static final Logger log = LoggerFactory.getLogger(BloqueoHorarioServiceIMP.class);

    private final BloqueoHorarioRepository bloqueoRepository;
    private final BarberoRepository barberoRepository;

    public BloqueoHorarioServiceIMP(BloqueoHorarioRepository bloqueoRepository,
                                    BarberoRepository barberoRepository) {
        this.bloqueoRepository = bloqueoRepository;
        this.barberoRepository = barberoRepository;
    }

    private BloqueoHorarioDTO toDTO(BloqueoHorario b) {
        BloqueoHorarioDTO dto = new BloqueoHorarioDTO();
        dto.setIdBloqueo(b.getIdBloqueo());
        dto.setIdBarbero(b.getBarbero() != null ? b.getBarbero().getIdBarbero() : null);
        dto.setFechaInicio(b.getFechaInicio());
        dto.setFechaFin(b.getFechaFin());
        dto.setMotivo(b.getMotivo());
        return dto;
    }

    private BloqueoHorario toEntity(BloqueoHorarioDTO dto) {
        BloqueoHorario b = new BloqueoHorario();
        b.setIdBloqueo(dto.getIdBloqueo());
        b.setFechaInicio(dto.getFechaInicio());
        b.setFechaFin(dto.getFechaFin());
        b.setMotivo(dto.getMotivo());
        if (dto.getIdBarbero() != null)
            b.setBarbero(barberoRepository.findById(dto.getIdBarbero())
                    .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + dto.getIdBarbero())));
        return b;
    }

    @Override
    public List<BloqueoHorarioDTO> findAll() {
        return bloqueoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public BloqueoHorarioDTO findById(Long id) {
        return bloqueoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Bloqueo no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public BloqueoHorarioDTO save(BloqueoHorarioDTO dto) {
        log.info("Creando bloqueo para barbero {}", dto.getIdBarbero());
        return toDTO(bloqueoRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando bloqueo con id: {}", id);
        bloqueoRepository.deleteById(id);
    }

    @Override
    public List<BloqueoHorarioDTO> findByBarbero(Long idBarbero) {
        return bloqueoRepository.findByBarbero(idBarbero).stream().map(this::toDTO).toList();
    }
}
