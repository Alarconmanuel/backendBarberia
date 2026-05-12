package com.barberia.controller;

import com.barberia.dto.AuthDTO;
import com.barberia.dto.UsuarioDTO;
import com.barberia.enums.RolEnum;
import com.barberia.model.Usuario;
import com.barberia.repository.UsuarioRepository;
import com.barberia.security.JwtUtils;
import com.barberia.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion con correo y contraseña")
    public ResponseEntity<AuthDTO.LoginResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getEmail()).orElse(null);

        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }

        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse(
                jwt,
                userDetails.getId(),
                userDetails.getNombre(),
                userDetails.getEmail(),
                userDetails.getRol()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario (por defecto rol CLIENTE)")
    public ResponseEntity<AuthDTO.RegisterResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        if (usuarioRepository.findByCorreo(request.getCorreoOTelefono()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreoOTelefono());
        usuario.setTelefono(null);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol() != null ? request.getRol() : RolEnum.CLIENTE);
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(usuario);

        AuthDTO.RegisterResponse response = new AuthDTO.RegisterResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getFechaRegistro()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener informacion del usuario autenticado")
    public ResponseEntity<AuthDTO.UserInfoResponse> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getEmail()).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        AuthDTO.UserInfoResponse response = new AuthDTO.UserInfoResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaRegistro()
        );
        return ResponseEntity.ok(response);
    }
}
