package com.barberia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServicioDTO {
    private Long idServicio;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private Double precio;

    @NotNull(message = "La duracion es obligatoria")
    @Min(value = 1, message = "Duracion minima 1 minuto")
    private Integer duracionMinutos;
}
