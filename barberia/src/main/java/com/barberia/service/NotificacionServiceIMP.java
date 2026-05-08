package com.barberia.service;

import com.barberia.dto.NotificacionDTO;
import com.barberia.enums.TipoNotificacionEnum;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.model.Cita;
import com.barberia.model.Notificacion;
import com.barberia.repository.CitaRepository;
import com.barberia.repository.NotificacionRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacionServiceIMP implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceIMP.class);

    private final NotificacionRepository notificacionRepository;
    private final CitaRepository citaRepository;

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;

    public NotificacionServiceIMP(NotificacionRepository notificacionRepository,
                                  CitaRepository citaRepository) {
        this.notificacionRepository = notificacionRepository;
        this.citaRepository = citaRepository;
    }

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.equals("TU_ACCOUNT_SID") && authToken != null && !authToken.equals("TU_AUTH_TOKEN")) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio inicializado correctamente");
        } else {
            log.warn("Twilio NO inicializado - usando credenciales de desarrollo");
        }
    }

    private NotificacionDTO toDTO(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(n.getIdNotificacion());
        dto.setIdCita(n.getCita() != null ? n.getCita().getIdCita() : null);
        dto.setTipo(n.getTipo());
        dto.setMensaje(n.getMensaje());
        dto.setEnviado(n.getEnviado());
        return dto;
    }

    private Notificacion toEntity(NotificacionDTO dto) {
        Notificacion n = new Notificacion();
        n.setIdNotificacion(dto.getIdNotificacion());
        n.setTipo(dto.getTipo());
        n.setEnviado(dto.getEnviado() != null ? dto.getEnviado() : false);
        if (dto.getIdCita() != null) {
            Cita cita = citaRepository.findById(dto.getIdCita())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + dto.getIdCita()));
            n.setCita(cita);
            if (dto.getMensaje() == null) {
                String msg = dto.getTipo() == TipoNotificacionEnum.CITA_AGENDADA
                        ? "Cita confirmada para el " + cita.getFecha() + " a las " + cita.getHoraInicio()
                        + " con " + cita.getBarbero().getNombre()
                        : "Tu cita del " + cita.getFecha() + " a las " + cita.getHoraInicio() + " fue cancelada.";
                n.setMensaje(msg);
            } else {
                n.setMensaje(dto.getMensaje());
            }
        }
        return n;
    }

    @Override
    public List<NotificacionDTO> findAll() {
        return notificacionRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public NotificacionDTO findById(Long id) {
        return notificacionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrada con id: " + id));
    }

    @Override
    @Transactional
    public NotificacionDTO save(NotificacionDTO dto) {
        Notificacion saved = notificacionRepository.save(toEntity(dto));

        if (!saved.getEnviado() && saved.getCita() != null && saved.getCita().getUsuario() != null) {
            enviarWhatsApp(saved);
        }

        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando notificacion con id: {}", id);
        notificacionRepository.deleteById(id);
    }

    @Override
    public List<NotificacionDTO> findByCita(Long idCita) {
        return notificacionRepository.findByCita(idCita).stream().map(this::toDTO).toList();
    }

    private void enviarWhatsApp(Notificacion notificacion) {
        try {
            String telefonoCliente = notificacion.getCita().getUsuario().getTelefono();
            if (telefonoCliente == null || telefonoCliente.isEmpty()) {
                log.warn("Usuario sin telefono, no se envia WhatsApp para notificacion {}", notificacion.getIdNotificacion());
                return;
            }

            if (accountSid == null || accountSid.equals("TU_ACCOUNT_SID")) {
                log.warn("Twilio no configurado, omitiendo envio de WhatsApp");
                return;
            }

            String toNumber = "whatsapp:+" + telefonoCliente;
            Message message = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(whatsappFrom),
                    notificacion.getMensaje()
            ).create();

            if (message.getStatus() == Message.Status.SENT || message.getStatus() == Message.Status.DELIVERED || message.getStatus() == Message.Status.QUEUED) {
                notificacion.setEnviado(true);
                notificacionRepository.save(notificacion);
                log.info("WhatsApp enviado exitosamente a {}", toNumber);
            } else {
                log.warn("WhatsApp no enviado. Estado: {}", message.getStatus());
            }
        } catch (Exception e) {
            log.error("Error al enviar WhatsApp: {}", e.getMessage());
        }
    }
}
