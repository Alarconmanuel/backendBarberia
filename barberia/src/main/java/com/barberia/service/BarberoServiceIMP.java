package com.barberia.service;

import com.barberia.dto.BarberoDTO;
import com.barberia.enums.EstadoCitaEnum;
import com.barberia.enums.TipoNotificacionEnum;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Barbero;
import com.barberia.model.Cita;
import com.barberia.model.Notificacion;
import com.barberia.model.Usuario;
import com.barberia.repository.BarberoRepository;
import com.barberia.repository.CitaRepository;
import com.barberia.repository.NotificacionRepository;
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
    private final CitaRepository citaRepository;
    private final NotificacionRepository notificacionRepository;
    private final TwilioService twilioService;

    public BarberoServiceIMP(BarberoRepository barberoRepository, UsuarioRepository usuarioRepository,
                             CitaRepository citaRepository, NotificacionRepository notificacionRepository,
                             TwilioService twilioService) {
        this.barberoRepository = barberoRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.notificacionRepository = notificacionRepository;
        this.twilioService = twilioService;
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
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + id));

        List<Cita> pendientes = citaRepository.findActivasByBarberoYFecha(id, java.time.LocalDate.now());
        for (Cita c : pendientes) {
            if (c.getEstado() == EstadoCitaEnum.PENDIENTE) {
                c.setEstado(EstadoCitaEnum.CANCELADA);
                citaRepository.save(c);
                try {
                    Notificacion n = new Notificacion();
                    n.setCita(c);
                    n.setTipo(TipoNotificacionEnum.CITA_CANCELADA);
                    n.setMensaje("Tu cita fue cancelada porque el barbero " + barbero.getNombre() + " ya no está disponible.");
                    n.setEnviado(false);
                    notificacionRepository.save(n);
                } catch (Exception e) {
                    log.warn("No se pudo notificar cancelación de cita {}: {}", c.getIdCita(), e.getMessage());
                }
                try {
                    if (c.getUsuario() != null && c.getUsuario().getTelefono() != null) {
                        twilioService.enviarWhatsAppCancelacionCita(
                                c.getUsuario().getTelefono(),
                                c.getUsuario().getNombre(),
                                barbero.getNombre(),
                                c.getFecha().toString(),
                                c.getHoraInicio().toString());
                    }
                } catch (Exception e) {
                    log.warn("No se pudo enviar WhatsApp de cancelación: {}", e.getMessage());
                }
            }
        }
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
