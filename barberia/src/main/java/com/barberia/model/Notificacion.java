package com.barberia.model;

import com.barberia.enums.TipoNotificacionEnum;
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
@Table(name = "notificacion") // tabla: notificacion
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    // FK a cita
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_cita") // FK: id_cita -> cita
    private Cita cita;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 30)
    private TipoNotificacionEnum tipo;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "enviado")
    private Boolean enviado = false;

    // before insert: asigna fecha de envio automaticamente
    @PrePersist
    public void prePersist() {
        this.fechaEnvio = LocalDateTime.now();
    }
}