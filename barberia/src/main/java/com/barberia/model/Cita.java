package com.barberia.model;

import com.barberia.enums.EstadoCitaEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cita") // tabla: cita
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    // FK a usuario (cliente)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_usuario") // FK: id_usuario -> usuario
    private Usuario usuario;

    // FK a barbero
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_barbero") // FK: id_barbero -> barbero
    private Barbero barbero;

    // FK a servicio
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_servicio") // FK: id_servicio -> servicio
    private Servicio servicio;

    // relacion inversa con notificaciones: al borrar cita se borran sus notificaciones
    @JsonIgnore
    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones = new ArrayList<>();

    // relacion inversa con resena: al borrar cita se borra su resena
    @JsonIgnore
    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private Resena resena;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoCitaEnum estado = EstadoCitaEnum.PENDIENTE;

    @Column(name = "hora_inicio_real")
    private LocalDateTime horaInicioReal;

    @Column(name = "hora_fin_real")
    private LocalDateTime horaFinReal;

    @Column(name = "no_presento")
    private Boolean noPresento = false;
}