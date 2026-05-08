package com.barberia.controller;

import com.barberia.dto.BloqueoHorarioDTO;
import com.barberia.service.BloqueoHorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bloqueos")
@Tag(name = "Bloqueos", description = "Gestion de bloqueos de horario")
public class BloqueoHorarioController {

    private final BloqueoHorarioService bloqueoService;

    public BloqueoHorarioController(BloqueoHorarioService bloqueoService) {
        this.bloqueoService = bloqueoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los bloqueos")
    public List<BloqueoHorarioDTO> getAll() {
        return bloqueoService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener bloqueo por ID")
    public ResponseEntity<BloqueoHorarioDTO> getById(@PathVariable Long id) {
        BloqueoHorarioDTO dto = bloqueoService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR', 'BARBERO')")
    @Operation(summary = "Crear bloqueo de horario")
    public BloqueoHorarioDTO create(@Valid @RequestBody BloqueoHorarioDTO dto) {
        return bloqueoService.save(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR', 'BARBERO')")
    @Operation(summary = "Eliminar bloqueo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bloqueoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barbero/{idBarbero}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: bloqueos de un barbero")
    public List<BloqueoHorarioDTO> getByBarbero(@PathVariable Long idBarbero) {
        return bloqueoService.findByBarbero(idBarbero);
    }
}
