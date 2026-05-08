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
@Table(name = "promedio_cliente") // tabla: promedio_cliente
public class PromedioCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promedio")
    private Long idPromedio;

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

    @Column(name = "duracion_promedio_minutos")
    private Double duracionPromedioMinutos = 35.0;

    @Column(name = "total_citas")
    private Integer totalCitas = 0;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    // before insert/update: actualiza timestamp automaticamente
    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}