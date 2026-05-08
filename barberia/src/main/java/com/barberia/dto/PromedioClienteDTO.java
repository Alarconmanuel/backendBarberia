package com.barberia.dto;

import lombok.Data;

@Data
public class PromedioClienteDTO {
    private Long idPromedio;
    private Long idUsuario; // solo el ID, no el objeto completo
    private Long idBarbero; // solo el ID, no el objeto completo
    private Double duracionPromedioMinutos;
    private Integer totalCitas;
}