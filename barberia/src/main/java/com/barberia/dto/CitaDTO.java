package com.barberia.dto;

import com.barberia.enums.EstadoCitaEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @FutureOrPresent(message = "La fecha debe ser futura o presente")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private String horaInicio;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaFin;
    private EstadoCitaEnum estado;
}
