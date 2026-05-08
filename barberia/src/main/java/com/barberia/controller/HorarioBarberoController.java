package com.barberia.controller;

import com.barberia.dto.HorarioBarberoDTO;
import com.barberia.service.HorarioBarberoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/horarios")
@Tag(name = "Horarios", description = "Gestion de horarios de barberos")
public class HorarioBarberoController {

    private final HorarioBarberoService horarioService;

    public HorarioBarberoController(HorarioBarberoService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los horarios")
    public List<HorarioBarberoDTO> getAll() {
        return horarioService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener horario por ID")
    public ResponseEntity<HorarioBarberoDTO> getById(@PathVariable Long id) {
        HorarioBarberoDTO dto = horarioService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Crear horario para un barbero")
    public HorarioBarberoDTO create(@Valid @RequestBody HorarioBarberoDTO dto) {
        return horarioService.save(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Actualizar horario")
    public ResponseEntity<HorarioBarberoDTO> update(@PathVariable Long id, @Valid @RequestBody HorarioBarberoDTO dto) {
        dto.setIdHorario(id);
        return ResponseEntity.ok(horarioService.save(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Eliminar horario")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        horarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barbero/{idBarbero}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: horarios de un barbero")
    public List<HorarioBarberoDTO> getByBarbero(@PathVariable Long idBarbero) {
        return horarioService.findByBarbero(idBarbero);
    }

    @GetMapping("/barbero/{idBarbero}/dia/{dia}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 2: horario de un barbero en un dia especifico")
    public List<HorarioBarberoDTO> getByBarberoYDia(@PathVariable Long idBarbero,
                                                    @PathVariable String dia) {
        return horarioService.findByBarberoYDia(idBarbero, dia);
    }
}
