package com.barberia.dto;

import com.barberia.enums.EstadoCitaEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaDTO {
    private Long idCita;

    @NotNull(message = "El usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El barbero es obligatorio")
    private Long idBarbero;

    @NotNull(message = "El servicio es obligatorio")
    private Long idServicio;

    @NotNull(message = "La fecha es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    private LocalTime horaFin;
    private EstadoCitaEnum estado;
}
