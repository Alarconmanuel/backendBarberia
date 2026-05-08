package com.barberia.dto;

import com.barberia.enums.RolEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "Correo invalido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Pattern(regexp = "^[0-9]{10}$", message = "Telefono debe tener 10 digitos")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "Minimo 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private RolEnum rol;

    private Boolean activo;
}
