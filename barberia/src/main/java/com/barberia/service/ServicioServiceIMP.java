package com.barberia.service;

import com.barberia.dto.ServicioDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Servicio;
import com.barberia.repository.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ServicioServiceIMP implements ServicioService {

    private static final Logger log = LoggerFactory.getLogger(ServicioServiceIMP.class);

    private final ServicioRepository servicioRepository;

    public ServicioServiceIMP(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    private ServicioDTO toDTO(Servicio s) {
        ServicioDTO dto = new ServicioDTO();
        dto.setIdServicio(s.getIdServicio());
        dto.setNombre(s.getNombre());
        dto.setDescripcion(s.getDescripcion());
        dto.setPrecio(s.getPrecio());
        dto.setDuracionMinutos(s.getDuracionMinutos());
        dto.setEspecialidad(s.getEspecialidad());
        return dto;
    }

    private Servicio toEntity(ServicioDTO dto) {
        Servicio s = new Servicio();
        s.setIdServicio(dto.getIdServicio());
        s.setNombre(dto.getNombre());
        s.setDescripcion(dto.getDescripcion());
        s.setPrecio(dto.getPrecio());
        s.setDuracionMinutos(dto.getDuracionMinutos());
        s.setEspecialidad(dto.getEspecialidad());
        return s;
    }

    @Override
    public List<ServicioDTO> findAll() {
        return servicioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public ServicioDTO findById(Long id) {
        return servicioRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public ServicioDTO save(ServicioDTO dto) {
        log.info("Creando/actualizando servicio: {}", dto.getNombre());
        return toDTO(servicioRepository.save(toEntity(dto)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando servicio con id: {}", id);
        servicioRepository.deleteById(id);
    }

    @Override
    public List<ServicioDTO> findByDuracionMenorIgual(Integer minutos) {
        return servicioRepository.findByDuracionMenorIgual(minutos).stream().map(this::toDTO).toList();
    }

    @Override
    public List<ServicioDTO> findByEspecialidad(String especialidad) {
        return servicioRepository.findByEspecialidadIgnoreCase(especialidad)
                .stream().map(this::toDTO).toList();
    }
}
