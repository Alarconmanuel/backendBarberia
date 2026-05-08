package com.barberia.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BloqueoHorarioDTO {
    private Long idBloqueo;
    private Long idBarbero;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String motivo;
}
