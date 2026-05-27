package com.barberia.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TwilioService {

    private static final Logger log = LoggerFactory.getLogger(TwilioService.class);
    private static final String PREFIJO_COLOMBIA = "+57";
    private static final String WHATSAPP_PREFIX = "whatsapp:";

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (accountSid == null || accountSid.isBlank() || "TU_ACCOUNT_SID".equals(accountSid)) {
            log.warn("Twilio no configurado - accountSid vacío. Las notificaciones WhatsApp estarán deshabilitadas.");
            return;
        }
        try {
            Twilio.init(accountSid, authToken);
            log.info("Twilio inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar Twilio: {}", e.getMessage());
        }
    }

    public void enviarWhatsAppCreacionCita(String telefonoCliente, String nombreCliente,
                                           String nombreBarbero, String nombreServicio,
                                           String fecha, String hora) {
        String mensaje = String.format(
                "Hola %s, tu cita en Barbería fue agendada con éxito:%n" +
                "Barbero: %s%n" +
                "Servicio: %s%n" +
                "Fecha: %s%n" +
                "Hora: %s%n%n" +
                "Te esperamos!",
                nombreCliente, nombreBarbero, nombreServicio, fecha, hora
        );
        enviar(telefonoCliente, mensaje);
    }

    public void enviarWhatsAppCancelacionCita(String telefonoCliente, String nombreCliente,
                                               String nombreBarbero, String fecha, String hora) {
        String mensaje = String.format(
                "Hola %s, tu cita en Barbería con %s del %s a las %s ha sido cancelada.%n%n" +
                "Si deseas agendar una nueva cita, comunícate con nosotros.",
                nombreCliente, nombreBarbero, fecha, hora
        );
        enviar(telefonoCliente, mensaje);
    }

    public void enviarWhatsAppCreacionCitaBarbero(String telefonoBarbero, String nombreBarbero,
                                                   String nombreCliente, String nombreServicio,
                                                   String fecha, String hora) {
        String mensaje = String.format(
                "Hola %s, tienes una nueva cita agendada:%n" +
                "Cliente: %s%n" +
                "Servicio: %s%n" +
                "Fecha: %s%n" +
                "Hora: %s%n%n" +
                "Prepárate para atenderlo!",
                nombreBarbero, nombreCliente, nombreServicio, fecha, hora
        );
        enviar(telefonoBarbero, mensaje);
    }

    public void enviarWhatsAppCancelacionCitaBarbero(String telefonoBarbero, String nombreBarbero,
                                                      String nombreCliente, String fecha, String hora) {
        String mensaje = String.format(
                "Hola %s, la cita con %s del %s a las %s ha sido cancelada.",
                nombreBarbero, nombreCliente, fecha, hora
        );
        enviar(telefonoBarbero, mensaje);
    }

    private void enviar(String telefono, String mensajeTexto) {
        if (accountSid == null || accountSid.isBlank() || "TU_ACCOUNT_SID".equals(accountSid)) {
            log.warn("Twilio no configurado. No se envió WhatsApp a {}", telefono);
            return;
        }
        try {
            String telefonoCompleto = formatearTelefono(telefono);
            String to = WHATSAPP_PREFIX + telefonoCompleto;
            String from = fromNumber.startsWith(WHATSAPP_PREFIX) ? fromNumber : WHATSAPP_PREFIX + fromNumber;

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    mensajeTexto
            ).create();

            log.info("WhatsApp enviado a {} (sid: {})", telefonoCompleto, message.getSid());
        } catch (Exception e) {
            log.error("Error al enviar WhatsApp a {}: {}", telefono, e.getMessage());
        }
    }

    private String formatearTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) return "";
        String limpio = telefono.replaceAll("[^\\d+]", "");
        if (limpio.startsWith("+")) return limpio;
        if (limpio.startsWith("57") && limpio.length() >= 12) return "+" + limpio;
        return PREFIJO_COLOMBIA + limpio;
    }
}
