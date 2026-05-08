package com.barberia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resena") // tabla: resena
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long idResena;

    // FK a cita (relacion 1:1)
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id_cita") // FK: id_cita -> cita
    private Cita cita;

    // FK a usuario
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_usuario") // FK: id_usuario -> usuario
    private Usuario usuario;

    // FK a barbero
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_barbero") // FK: id_barbero -> barbero
    private Barbero barbero;

    @Column(name = "calificacion")
    private Integer calificacion;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    // before insert: asigna fecha automaticamente
    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}