package com.barberia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BarberoDTO {
    private Long idBarbero;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String especialidad;
    private String telefono;
    private Boolean activo;
    private Long idUsuario;
}
