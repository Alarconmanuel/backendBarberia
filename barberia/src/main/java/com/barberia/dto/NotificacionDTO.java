package com.barberia.dto;

import com.barberia.enums.TipoNotificacionEnum;
import lombok.Data;

@Data
public class NotificacionDTO {
    private Long idNotificacion;
    private Long idCita; // solo el ID, no el objeto completo
    private TipoNotificacionEnum tipo;
    private String mensaje;
    private Boolean enviado;
}