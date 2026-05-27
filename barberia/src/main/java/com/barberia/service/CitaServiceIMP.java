package com.barberia.service;

import com.barberia.dto.CitaDTO;
import com.barberia.enums.DiaSemanaEnum;
import com.barberia.enums.EstadoCitaEnum;
import com.barberia.enums.TipoNotificacionEnum;
import com.barberia.exception.BadRequestException;
import com.barberia.exception.ConflictException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.*;
import com.barberia.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final BloqueoHorarioRepository bloqueoHorarioRepository;
    private final NotificacionRepository notificacionRepository;
    private final TwilioService twilioService;

    public CitaServiceIMP(CitaRepository citaRepository,
                          UsuarioRepository usuarioRepository,
                          BarberoRepository barberoRepository,
                          ServicioRepository servicioRepository,
                          PromedioClienteRepository promedioClienteRepository,
                          HorarioBarberoRepository horarioBarberoRepository,
                          BloqueoHorarioRepository bloqueoHorarioRepository,
                          NotificacionRepository notificacionRepository,
                          TwilioService twilioService) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.promedioClienteRepository = promedioClienteRepository;
        this.horarioBarberoRepository = horarioBarberoRepository;
        this.bloqueoHorarioRepository = bloqueoHorarioRepository;
        this.notificacionRepository = notificacionRepository;
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

        validarRecursos(dto);

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getIdUsuario()));
        Barbero barbero = barberoRepository.findById(dto.getIdBarbero())
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + dto.getIdBarbero()));
        Servicio servicio = servicioRepository.findById(dto.getIdServicio())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + dto.getIdServicio()));

        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new BadRequestException("El barbero seleccionado no está disponible actualmente");
        }

        String espServicio = servicio.getEspecialidad() != null ? servicio.getEspecialidad().trim() : null;
        String espBarbero = barbero.getEspecialidad() != null ? barbero.getEspecialidad().trim() : null;
        if (espServicio != null && espBarbero != null && !espServicio.equalsIgnoreCase(espBarbero)) {
            throw new BadRequestException("El servicio seleccionado no corresponde a la especialidad del barbero");
        }

        LocalDate fecha = dto.getFecha();
        if (fecha.isBefore(LocalDate.now())) {
            throw new BadRequestException("La fecha no puede ser anterior a hoy");
        }

        LocalTime horaInicio = parseHoraFlexible(dto.getHoraInicio());
        int minutos = Math.max(servicio.getDuracionMinutos(), 35);
        LocalTime horaFin = horaInicio.plusMinutes(minutos);

        validarHorarioLaboral(barbero.getIdBarbero(), fecha, horaInicio, horaFin);
        validarSinBloqueo(barbero.getIdBarbero(), fecha, horaInicio, horaFin);
        validarSinConflicto(barbero.getIdBarbero(), fecha, horaInicio, horaFin);

        Cita cita = new Cita();
        cita.setFecha(fecha);
        cita.setHoraInicio(horaInicio);
        cita.setHoraFin(horaFin);
        cita.setEstado(EstadoCitaEnum.PENDIENTE);
        cita.setUsuario(usuario);
        cita.setBarbero(barbero);
        cita.setServicio(servicio);

        CitaDTO saved = toDTO(citaRepository.save(cita));

        try {
            crearNotificacion(saved.getIdCita(), TipoNotificacionEnum.CITA_AGENDADA,
                    "Cita confirmada para el " + fecha + " a las " + horaInicio + " con " + barbero.getNombre());
        } catch (Exception e) {
            log.warn("No se pudo crear notificación: {}", e.getMessage());
        }

        try {
            if (usuario.getTelefono() != null) {
                twilioService.enviarWhatsAppCreacionCita(
                        usuario.getTelefono(), usuario.getNombre(),
                        barbero.getNombre(), servicio.getNombre(),
                        fecha.toString(), horaInicio.toString());
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

        if (c.getEstado() == EstadoCitaEnum.FINALIZADA) {
            throw new BadRequestException("No se puede cancelar una cita que ya fue finalizada");
        }
        if (c.getEstado() == EstadoCitaEnum.CANCELADA) {
            throw new BadRequestException("La cita ya se encuentra cancelada");
        }

        log.info("Cancelando cita {}", id);
        c.setEstado(EstadoCitaEnum.CANCELADA);
        CitaDTO saved = toDTO(citaRepository.save(c));

        try {
            crearNotificacion(id, TipoNotificacionEnum.CITA_CANCELADA,
                    "Tu cita del " + c.getFecha() + " a las " + c.getHoraInicio() + " fue cancelada.");
        } catch (Exception e) {
            log.warn("No se pudo crear notificación de cancelación: {}", e.getMessage());
        }

        try {
            Usuario usuario = c.getUsuario();
            Barbero barbero = c.getBarbero();
            if (usuario != null && usuario.getTelefono() != null) {
                twilioService.enviarWhatsAppCancelacionCita(
                        usuario.getTelefono(), usuario.getNombre(),
                        barbero != null ? barbero.getNombre() : "Barbero",
                        c.getFecha().toString(), c.getHoraInicio().toString());
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
        if (c.getEstado() != EstadoCitaEnum.PENDIENTE) {
            throw new BadRequestException("Solo se puede iniciar una cita en estado PENDIENTE (actual: " + c.getEstado() + ")");
        }
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
        if (c.getEstado() != EstadoCitaEnum.EN_CURSO) {
            throw new BadRequestException("Solo se puede finalizar una cita en curso (actual: " + c.getEstado() + ")");
        }
        log.info("Finalizando cita {}", id);
        c.setEstado(EstadoCitaEnum.FINALIZADA);
        c.setHoraFinReal(LocalDateTime.now());
        CitaDTO saved = toDTO(citaRepository.save(c));
        if (c.getHoraInicioReal() != null) {
            long duracion = ChronoUnit.MINUTES.between(c.getHoraInicioReal(), c.getHoraFinReal());
            log.info("Cita {} finalizada. Duracion real: {} minutos", id, duracion);
            actualizarPromedio(c.getUsuario().getIdUsuario(), c.getBarbero().getIdBarbero(), duracion);
        }
        return saved;
    }

    @Override
    @Transactional
    public CitaDTO marcarNoPresento(Long id) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        if (c.getEstado() != EstadoCitaEnum.PENDIENTE && c.getEstado() != EstadoCitaEnum.EN_CURSO) {
            throw new BadRequestException("Solo se puede marcar como no presentado una cita PENDIENTE o EN_CURSO (actual: " + c.getEstado() + ")");
        }
        log.info("Marcando cita {} como no presentado", id);
        c.setEstado(EstadoCitaEnum.NO_PRESENTADO);
        c.setNoPresento(true);
        return toDTO(citaRepository.save(c));
    }

    @Override
    public List<String> getDisponibilidad(Long idBarbero, LocalDate fecha) {
        List<String> slots = new ArrayList<>();
        Barbero barbero = barberoRepository.findById(idBarbero)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con id: " + idBarbero));

        DiaSemanaEnum diaSemana = localDateToDiaSemana(fecha);
        List<HorarioBarbero> horarios = horarioBarberoRepository.findByBarberoYDia(idBarbero, diaSemana.name());

        if (horarios.isEmpty()) {
            log.info("No hay horarios definidos para barbero {} el día {}", idBarbero, diaSemana);
            return slots;
        }

        List<Cita> ocupadas = citaRepository.findActivasByBarberoYFecha(idBarbero, fecha);

        List<BloqueoHorario> bloqueos = bloqueoHorarioRepository.findBloqueoEnRango(
                idBarbero, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());

        for (HorarioBarbero horario : horarios) {
            LocalTime actual = horario.getHoraInicio();
            while (actual.isBefore(horario.getHoraFin())) {
                LocalTime siguiente = actual.plusMinutes(30);
                if (fecha.equals(LocalDate.now()) && actual.isBefore(LocalTime.now())) {
                    actual = siguiente;
                    continue;
                }
                if (estaOcupado(actual, siguiente, ocupadas)) {
                    actual = siguiente;
                    continue;
                }
                if (estaBloqueado(barbero, fecha, actual, siguiente, bloqueos)) {
                    actual = siguiente;
                    continue;
                }
                slots.add(actual.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                actual = siguiente;
            }
        }
        return slots;
    }

    private void validarRecursos(CitaDTO dto) {
        if (dto.getIdUsuario() == null) throw new BadRequestException("El usuario es obligatorio");
        if (dto.getIdBarbero() == null) throw new BadRequestException("El barbero es obligatorio");
        if (dto.getIdServicio() == null) throw new BadRequestException("El servicio es obligatorio");
        if (dto.getFecha() == null) throw new BadRequestException("La fecha es obligatoria");
        if (dto.getHoraInicio() == null || dto.getHoraInicio().isBlank()) throw new BadRequestException("La hora de inicio es obligatoria");
    }

    private void validarHorarioLaboral(Long idBarbero, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        DiaSemanaEnum diaSemana = localDateToDiaSemana(fecha);
        List<HorarioBarbero> horarios = horarioBarberoRepository.findByBarberoYDia(idBarbero, diaSemana.name());
        if (horarios.isEmpty()) {
            throw new BadRequestException("El barbero no trabaja el día seleccionado");
        }
        boolean dentro = false;
        for (HorarioBarbero h : horarios) {
            if (!horaInicio.isBefore(h.getHoraInicio()) && !horaFin.isAfter(h.getHoraFin())) {
                dentro = true;
                break;
            }
        }
        if (!dentro) {
            throw new BadRequestException("La hora solicitada está fuera del horario laboral del barbero");
        }
    }

    private void validarSinBloqueo(Long idBarbero, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
        LocalDateTime fin = LocalDateTime.of(fecha, horaFin);
        List<BloqueoHorario> bloqueos = bloqueoHorarioRepository.findBloqueoEnRango(idBarbero, inicio, fin);
        if (!bloqueos.isEmpty()) {
            throw new BadRequestException("El barbero tiene un bloqueo en el horario solicitado");
        }
    }

    private void validarSinConflicto(Long idBarbero, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        List<Cita> activas = citaRepository.findActivasByBarberoYFecha(idBarbero, fecha);
        for (Cita c : activas) {
            if (horaInicio.isBefore(c.getHoraFin()) && horaFin.isAfter(c.getHoraInicio())) {
                throw new ConflictException("Horario no disponible: existe conflicto con otra cita");
            }
        }
    }

    private boolean estaOcupado(LocalTime inicio, LocalTime fin, List<Cita> ocupadas) {
        for (Cita c : ocupadas) {
            if (c.getHoraInicio() != null && c.getHoraFin() != null &&
                inicio.isBefore(c.getHoraFin()) && fin.isAfter(c.getHoraInicio())) {
                return true;
            }
        }
        return false;
    }

    private boolean estaBloqueado(Barbero barbero, LocalDate fecha, LocalTime inicio, LocalTime fin,
                                  List<BloqueoHorario> bloqueos) {
        LocalDateTime start = LocalDateTime.of(fecha, inicio);
        LocalDateTime end = LocalDateTime.of(fecha, fin);
        for (BloqueoHorario b : bloqueos) {
            if (start.isBefore(b.getFechaFin()) && end.isAfter(b.getFechaInicio())) {
                return true;
            }
        }
        return false;
    }

    private DiaSemanaEnum localDateToDiaSemana(LocalDate fecha) {
        DayOfWeek day = fecha.getDayOfWeek();
        return switch (day) {
            case MONDAY -> DiaSemanaEnum.LUNES;
            case TUESDAY -> DiaSemanaEnum.MARTES;
            case WEDNESDAY -> DiaSemanaEnum.MIERCOLES;
            case THURSDAY -> DiaSemanaEnum.JUEVES;
            case FRIDAY -> DiaSemanaEnum.VIERNES;
            case SATURDAY -> DiaSemanaEnum.SABADO;
            case SUNDAY -> DiaSemanaEnum.DOMINGO;
        };
    }

    private LocalTime parseHoraFlexible(String hora) {
        if (hora == null || hora.isBlank()) {
            throw new BadRequestException("La hora es obligatoria");
        }
        try {
            return LocalTime.parse(hora);
        } catch (Exception e) {
            try {
                return LocalTime.parse(hora + ":00");
            } catch (Exception ex) {
                throw new BadRequestException("Formato de hora inválido. Use HH:mm o HH:mm:ss");
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

    private void crearNotificacion(Long idCita, TipoNotificacionEnum tipo, String mensaje) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + idCita));
        Notificacion n = new Notificacion();
        n.setCita(cita);
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        n.setEnviado(false);
        notificacionRepository.save(n);
    }
}
