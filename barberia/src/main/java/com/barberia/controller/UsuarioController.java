package com.barberia.controller;

import com.barberia.dto.UsuarioDTO;
import com.barberia.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD y consultas de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Listar todos los usuarios")
    public List<UsuarioDTO> getAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id) {
        UsuarioDTO dto = usuarioService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Crear usuario")
    public UsuarioDTO create(@Valid @RequestBody UsuarioDTO dto) {
        return usuarioService.save(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR') or #id == authentication.principal.id")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        dto.setIdUsuario(id);
        return ResponseEntity.ok(usuarioService.save(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Consulta nativa 1: usuarios activos por rol")
    public List<UsuarioDTO> getActivosByRol(@PathVariable String rol) {
        return usuarioService.findActivosByRol(rol);
    }
}
