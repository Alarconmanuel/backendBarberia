package com.barberia.service;

import com.barberia.dto.HorarioBarberoDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.HorarioBarbero;
import com.barberia.repository.BarberoRepository;
import com.barberia.repository.HorarioBarberoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HorarioBarberoServiceIMP implements HorarioBarberoService {

    private static final Logger log = LoggerFactory.getLogger(HorarioBarberoServiceIMP.class);

    private final HorarioBarberoRepository horarioRepository;
    private final BarberoRepository barberoRepository;

    public HorarioBarberoServiceIMP(HorarioBarberoRepository horarioRepository,
                                    BarberoRepository barberoRepository) {
        this.horarioRepository = horarioRepository;
        this.barberoRepository = barberoRepository;
    }

    private HorarioBarberoDTO toDTO(HorarioBarbero h) {
        HorarioBarberoDTO dto = new HorarioBarberoDTO();
        dto.setIdHorario(h.getIdHorario());
        dto.setIdBarbero(h.getBarbero() != null ? h.getBarbero().getIdBarbero() : null);
        dto.setDiaSemana(h.getDiaSemana());
        dto.setHoraInicio(h.getHoraInicio());
        dto.setHoraFin(h.getHoraFin());
        return dto;
    }

    private HorarioBarbero toEntity(HorarioBarberoDTO dto) {
        HorarioBarbero h = new HorarioBarbero();
        h.setIdHorario(dto.getIdHorario());
        h.setDiaSemana(dto.getDiaSemana());
        h.setHoraInicio(dto.getHoraInicio());
        h.setHoraFin(dto.getHoraFin());
        if (dto.getIdBarbero() != null)
            h.setBarbero(barberoRepository.findById(dto.getIdBarbero())
                    .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + dto.getIdBarbero())));
        return h;
    }

    @Override
    public List<HorarioBarberoDTO> findAll() {
        return horarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public HorarioBarberoDTO findById(Long id) {
        return horarioRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public HorarioBarberoDTO save(HorarioBarberoDTO dto) {
        log.info("Creando/actualizando horario para barbero {}", dto.getIdBarbero());
        return toDTO(horarioRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando horario con id: {}", id);
        horarioRepository.deleteById(id);
    }

    @Override
    public List<HorarioBarberoDTO> findByBarbero(Long idBarbero) {
        return horarioRepository.findByBarbero(idBarbero).stream().map(this::toDTO).toList();
    }

    @Override
    public List<HorarioBarberoDTO> findByBarberoYDia(Long idBarbero, String dia) {
        return horarioRepository.findByBarberoYDia(idBarbero, dia).stream().map(this::toDTO).toList();
    }
}
