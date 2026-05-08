package com.barberia.controller;

import com.barberia.dto.BarberoDTO;
import com.barberia.service.BarberoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/barberos")
@Tag(name = "Barberos", description = "CRUD y consultas de barberos")
public class BarberoController {

    private final BarberoService barberoService;

    public BarberoController(BarberoService barberoService) {
        this.barberoService = barberoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los barberos")
    public List<BarberoDTO> getAll() {
        return barberoService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener barbero por ID")
    public ResponseEntity<BarberoDTO> getById(@PathVariable Long id) {
        BarberoDTO dto = barberoService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Crear barbero")
    public BarberoDTO create(@Valid @RequestBody BarberoDTO dto) {
        return barberoService.save(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Actualizar barbero")
    public ResponseEntity<BarberoDTO> update(@PathVariable Long id, @Valid @RequestBody BarberoDTO dto) {
        dto.setIdBarbero(id);
        return ResponseEntity.ok(barberoService.save(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Eliminar barbero")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        barberoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activos")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: barberos activos")
    public List<BarberoDTO> getActivos() {
        return barberoService.findActivos();
    }

    @GetMapping("/mas-solicitados")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 2: barberos mas solicitados")
    public List<BarberoDTO> getMasSolicitados() {
        return barberoService.findMasSolicitados();
    }
}
