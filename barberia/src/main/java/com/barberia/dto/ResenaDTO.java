package com.barberia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResenaDTO {
    private Long idResena;

    @NotNull(message = "La cita es obligatoria")
    private Long idCita;

    @NotNull(message = "El usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El barbero es obligatorio")
    private Long idBarbero;

    @NotNull(message = "La calificacion es obligatoria")
    @Min(value = 1, message = "Calificacion minima 1")
    @Max(value = 5, message = "Calificacion maxima 5")
    private Integer calificacion;

    @Size(max = 500, message = "Maximo 500 caracteres")
    private String comentario;
}
