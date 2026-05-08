package com.barberia.model;

import com.barberia.enums.RolEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario") // tabla: usuario
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "correo", length = 150, unique = true)
    private String correo;

    @Column(name = "telefono", length = 20, unique = true)
    private String telefono;

    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 20)
    private RolEnum rol;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // before insert: asigna fecha de registro automaticamente
    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}