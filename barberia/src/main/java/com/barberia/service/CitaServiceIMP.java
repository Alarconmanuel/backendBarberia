package com.barberia.service;

import com.barberia.dto.CitaDTO;
import com.barberia.enums.EstadoCitaEnum;
import com.barberia.exception.BadRequestException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.*;
import com.barberia.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceIMP implements CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaServiceIMP.class);

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final PromedioClienteRepository promedioClienteRepository;
    private final HorarioBarberoRepository horarioBarberoRepository;
    private final TwilioService twilioService;

    public CitaServiceIMP(CitaRepository citaRepository,
                          UsuarioRepository usuarioRepository,
                          BarberoRepository barberoRepository,
                          ServicioRepository servicioRepository,
                          PromedioClienteRepository promedioClienteRepository,
                          HorarioBarberoRepository horarioBarberoRepository,
                          TwilioService twilioService) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.promedioClienteRepository = promedioClienteRepository;
        this.horarioBarberoRepository = horarioBarberoRepository;
        this.twilioService = twilioService;
    }

    private CitaDTO toDTO(Cita c) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(c.getIdCita());
        dto.setIdUsuario(c.getUsuario() != null ? c.getUsuario().getIdUsuario() : null);
        dto.setIdBarbero(c.getBarbero() != null ? c.getBarbero().getIdBarbero() : null);
        dto.setIdServicio(c.getServicio() != null ? c.getServicio().getIdServicio() : null);
        dto.setFecha(c.getFecha());
        dto.setHoraInicio(c.getHoraInicio() != null ? c.getHoraInicio().toString() : null);
        dto.setHoraFin(c.getHoraFin());
        dto.setEstado(c.getEstado());
        return dto;
    }

    private Cita toEntity(CitaDTO dto) {
        Cita c = new Cita();
        c.setIdCita(dto.getIdCita());
        c.setFecha(dto.getFecha());
        c.setHoraInicio(parseHoraFlexible(dto.getHoraInicio()));
        c.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoCitaEnum.PENDIENTE);
        if (dto.getIdUsuario() != null)
            c.setUsuario(usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getIdUsuario())));
        if (dto.getIdBarbero() != null)
            c.setBarbero(barberoRepository.findById(dto.getIdBarbero())
                    .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + dto.getIdBarbero())));
        if (dto.getIdServicio() != null)
            c.setServicio(servicioRepository.findById(dto.getIdServicio())
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + dto.getIdServicio())));
        return c;
    }

    @Override
    public List<CitaDTO> findAll() {
        return citaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public CitaDTO findById(Long id) {
        return citaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
    }

    @Override
    @Transactional
    public CitaDTO save(CitaDTO dto) {
        log.info("Creando cita para usuario {} con barbero {}", dto.getIdUsuario(), dto.getIdBarbero());
        Cita cita = toEntity(dto);
        int minutos = calcularMinutos(dto.getIdUsuario(), dto.getIdBarbero(), cita.getServicio());
        LocalTime horaInicio = parseHoraFlexible(dto.getHoraInicio());
        cita.setHoraFin(horaInicio.plusMinutes(minutos));
        verificarDisponibilidad(dto.getIdBarbero(), dto.getFecha(), horaInicio, cita.getHoraFin());
        CitaDTO saved = toDTO(citaRepository.save(cita));
        try {
            Usuario usuario = cita.getUsuario();
            Barbero barbero = cita.getBarbero();
            Servicio servicio = cita.getServicio();
            if (usuario != null && usuario.getTelefono() != null) {
                twilioService.enviarWhatsAppCreacionCita(
                        usuario.getTelefono(),
                        usuario.getNombre(),
                        barbero != null ? barbero.getNombre() : "Barbero",
                        servicio != null ? servicio.getNombre() : "Servicio",
                        cita.getFecha().toString(),
                        cita.getHoraInicio().toString()
                );
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación WhatsApp: {}", e.getMessage());
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando cita con id: {}", id);
        citaRepository.deleteById(id);
    }

    @Override
    public List<CitaDTO> findByUsuario(Long idUsuario) {
        return citaRepository.findByUsuario(idUsuario).stream().map(this::toDTO).toList();
    }

    @Override
    public List<CitaDTO> findByBarberoYFecha(Long idBarbero, LocalDate fecha) {
        return citaRepository.findByBarberoYFecha(idBarbero, fecha).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public CitaDTO cancelar(Long id) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        log.info("Cancelando cita {}", id);
        c.setEstado(EstadoCitaEnum.CANCELADA);
        CitaDTO saved = toDTO(citaRepository.save(c));
        try {
            Usuario usuario = c.getUsuario();
            Barbero barbero = c.getBarbero();
            if (usuario != null && usuario.getTelefono() != null) {
                twilioService.enviarWhatsAppCancelacionCita(
                        usuario.getTelefono(),
                        usuario.getNombre(),
                        barbero != null ? barbero.getNombre() : "Barbero",
                        c.getFecha().toString(),
                        c.getHoraInicio().toString()
                );
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de cancelación WhatsApp: {}", e.getMessage());
        }
        return saved;
    }

    @Override
    @Transactional
    public CitaDTO iniciar(Long id) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        log.info("Iniciando cita {}", id);
        c.setEstado(EstadoCitaEnum.EN_CURSO);
        c.setHoraInicioReal(LocalDateTime.now());
        return toDTO(citaRepository.save(c));
    }

    @Override
    @Transactional
    public CitaDTO finalizar(Long id) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        log.info("Finalizando cita {}", id);
        c.setEstado(EstadoCitaEnum.FINALIZADA);
        c.setHoraFinReal(LocalDateTime.now());
        if (c.getHoraInicioReal() != null) {
            long duracion = ChronoUnit.MINUTES.between(c.getHoraInicioReal(), c.getHoraFinReal());
            log.info("Cita {} finalizada. Duracion real: {} minutos", id, duracion);
            actualizarPromedio(c.getUsuario().getIdUsuario(), c.getBarbero().getIdBarbero(), duracion);
        }
        return toDTO(citaRepository.save(c));
    }

    @Override
    @Transactional
    public CitaDTO marcarNoPresento(Long id) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        log.info("Marcando cita {} como no presentado", id);
        c.setEstado(EstadoCitaEnum.NO_PRESENTADO);
        c.setNoPresento(true);
        return toDTO(citaRepository.save(c));
    }

    public List<LocalTime> getDisponibilidad(Long idBarbero, LocalDate fecha) {
        List<LocalTime> libres = new ArrayList<>();
        List<Cita> ocupadas = citaRepository.findByBarberoYFecha(idBarbero, fecha);
        
        LocalTime inicioJornada = LocalTime.of(9, 0);
        LocalTime finJornada = LocalTime.of(18, 0);
        
        LocalTime actual = inicioJornada;
        while (actual.isBefore(finJornada)) {
            boolean ocupado = false;
            LocalTime siguiente = actual.plusMinutes(30);
            
            for (Cita c : ocupadas) {
                if (c.getHoraInicio() != null && c.getHoraFin() != null && 
                    actual.isBefore(c.getHoraFin()) && siguiente.isAfter(c.getHoraInicio())) {
                    ocupado = true;
                    break;
                }
            }
            
            if (!ocupado) libres.add(actual);
            actual = actual.plusMinutes(30);
        }
        return libres;
    }

    private LocalTime parseHoraFlexible(String hora) {
        if (hora == null || hora.isBlank()) {
            throw new IllegalArgumentException("La hora es obligatoria");
        }
        try {
            return LocalTime.parse(hora);
        } catch (Exception e) {
            try {
                return LocalTime.parse(hora + ":00");
            } catch (Exception ex) {
                throw new IllegalArgumentException("Formato de hora inválido. Use HH:mm o HH:mm:ss");
            }
        }
    }

    private int calcularMinutos(Long idUsuario, Long idBarbero, Servicio servicio) {
        if (idUsuario != null && idBarbero != null) {
            Optional<PromedioCliente> p = promedioClienteRepository.findByUsuarioYBarbero(idUsuario, idBarbero);
            if (p.isPresent() && p.get().getTotalCitas() > 0)
                return (int) Math.ceil(p.get().getDuracionPromedioMinutos());
        }
        return servicio != null ? Math.max(servicio.getDuracionMinutos(), 35) : 35;
    }

    private void verificarDisponibilidad(Long idBarbero, LocalDate fecha,
                                         LocalTime horaInicio, LocalTime horaFin) {
        List<Cita> activas = citaRepository.findActivasByBarberoYFecha(idBarbero, fecha);
        for (Cita c : activas) {
            if (horaInicio.isBefore(c.getHoraFin()) && horaFin.isAfter(c.getHoraInicio())) {
                log.warn("Intento de agendar en horario no disponible: barbero {}, fecha {}", idBarbero, fecha);
                throw new BadRequestException("Horario no disponible: existe conflicto con cita existente");
            }
        }
    }

    private void actualizarPromedio(Long idUsuario, Long idBarbero, long duracion) {
        PromedioCliente p = promedioClienteRepository.findByUsuarioYBarbero(idUsuario, idBarbero)
                .orElseGet(() -> {
                    PromedioCliente nuevo = new PromedioCliente();
                    nuevo.setUsuario(usuarioRepository.findById(idUsuario)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario)));
                    nuevo.setBarbero(barberoRepository.findById(idBarbero)
                            .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + idBarbero)));
                    nuevo.setTotalCitas(0);
                    nuevo.setDuracionPromedioMinutos(35.0);
                    return nuevo;
                });
        double nuevo = ((p.getDuracionPromedioMinutos() * p.getTotalCitas()) + duracion) / (p.getTotalCitas() + 1);
        p.setDuracionPromedioMinutos(nuevo);
        p.setTotalCitas(p.getTotalCitas() + 1);
        promedioClienteRepository.save(p);
    }
}
