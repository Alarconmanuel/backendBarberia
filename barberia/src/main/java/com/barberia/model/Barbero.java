package com.barberia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "barbero") // tabla: barbero
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barbero")
    private Long idBarbero;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "activo")
    private Boolean activo = true;

    // FK a usuario
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_usuario") // FK: id_usuario -> usuario
    private Usuario usuario;
}