package com.barberia.model;

import com.barberia.enums.DiaSemanaEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "horario_barbero") // tabla: horario_barbero
public class HorarioBarbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Long idHorario;

    // FK a barbero
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_barbero") // FK: id_barbero -> barbero
    private Barbero barbero;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", length = 15)
    private DiaSemanaEnum diaSemana;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;
}