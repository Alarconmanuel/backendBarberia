package com.barberia.dto;

import com.barberia.enums.DiaSemanaEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioBarberoDTO {
    private Long idHorario;

    @NotNull(message = "El barbero es obligatorio")
    private Long idBarbero;

    @NotNull(message = "El dia de la semana es obligatorio")
    private DiaSemanaEnum diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
}
