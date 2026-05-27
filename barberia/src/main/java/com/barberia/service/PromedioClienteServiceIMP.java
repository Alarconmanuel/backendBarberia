package com.barberia.service;

import com.barberia.dto.PromedioClienteDTO;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.PromedioCliente;
import com.barberia.repository.PromedioClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PromedioClienteServiceIMP implements PromedioClienteService {

    private static final Logger log = LoggerFactory.getLogger(PromedioClienteServiceIMP.class);

    private final PromedioClienteRepository promedioRepository;

    public PromedioClienteServiceIMP(PromedioClienteRepository promedioRepository) {
        this.promedioRepository = promedioRepository;
    }

    private PromedioClienteDTO toDTO(PromedioCliente p) {
        PromedioClienteDTO dto = new PromedioClienteDTO();
        dto.setIdPromedio(p.getIdPromedio());
        dto.setIdUsuario(p.getUsuario() != null ? p.getUsuario().getIdUsuario() : null);
        dto.setIdBarbero(p.getBarbero() != null ? p.getBarbero().getIdBarbero() : null);
        dto.setDuracionPromedioMinutos(p.getDuracionPromedioMinutos());
        dto.setTotalCitas(p.getTotalCitas());
        return dto;
    }

    @Override
    public List<PromedioClienteDTO> findAll() {
        return promedioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public PromedioClienteDTO findById(Long id) {
        return promedioRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Promedio no encontrado con id: " + id));
    }

    @Override
    public PromedioClienteDTO findByUsuarioYBarbero(Long idUsuario, Long idBarbero) {
        return promedioRepository.findByUsuarioYBarbero(idUsuario, idBarbero)
                .map(this::toDTO)
                .orElseGet(() -> {
                    PromedioClienteDTO dto = new PromedioClienteDTO();
                    dto.setIdUsuario(idUsuario);
                    dto.setIdBarbero(idBarbero);
                    dto.setDuracionPromedioMinutos(0.0);
                    dto.setTotalCitas(0);
                    return dto;
                });
    }
}
