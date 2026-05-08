package com.barberia.dto;

import com.barberia.enums.RolEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo invalido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "El correo o telefono es obligatorio")
        private String correoOTelefono;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "Mínimo 6 caracteres")
        private String password;

        private RolEnum rol;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String type = "Bearer";
        private Long idUsuario;
        private String nombre;
        private String correo;
        private RolEnum rol;

        public LoginResponse(String token, Long idUsuario, String nombre, String correo, RolEnum rol) {
            this.token = token;
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.correo = correo;
            this.rol = rol;
        }
    }

    @Data
    public static class RegisterResponse {
        private Long idUsuario;
        private String nombre;
        private String correo;
        private RolEnum rol;
        private LocalDateTime fechaRegistro;

        public RegisterResponse(Long idUsuario, String nombre, String correo, RolEnum rol, LocalDateTime fechaRegistro) {
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.correo = correo;
            this.rol = rol;
            this.fechaRegistro = fechaRegistro;
        }
    }

    @Data
    public static class UserInfoResponse {
        private Long idUsuario;
        private String nombre;
        private String correo;
        private String telefono;
        private RolEnum rol;
        private Boolean activo;
        private LocalDateTime fechaRegistro;

        public UserInfoResponse(Long idUsuario, String nombre, String correo, String telefono,
                                RolEnum rol, Boolean activo, LocalDateTime fechaRegistro) {
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.correo = correo;
            this.telefono = telefono;
            this.rol = rol;
            this.activo = activo;
            this.fechaRegistro = fechaRegistro;
        }
    }
}
