package com.barberia.controller;

import com.barberia.dto.CitaDTO;
import com.barberia.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Gestion completa de citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Listar todas las citas")
    public List<CitaDTO> getAll() {
        return citaService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener cita por ID")
    public ResponseEntity<CitaDTO> getById(@PathVariable Long id) {
        CitaDTO dto = citaService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'SUPERADMIN')")
    @Operation(summary = "Crear cita (calcula hora_fin automaticamente segun promedio del cliente)")
    public CitaDTO create(@Valid @RequestBody CitaDTO dto) {
        return citaService.save(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Actualizar cita")
    public ResponseEntity<CitaDTO> update(@PathVariable Long id, @Valid @RequestBody CitaDTO dto) {
        dto.setIdCita(id);
        return ResponseEntity.ok(citaService.save(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Eliminar cita")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        citaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: citas de un cliente")
    public List<CitaDTO> getByUsuario(@PathVariable Long idUsuario) {
        return citaService.findByUsuario(idUsuario);
    }

    @GetMapping("/barbero/{idBarbero}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 2: citas de un barbero en una fecha")
    public List<CitaDTO> getByBarberoYFecha(
            @PathVariable Long idBarbero,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.findByBarberoYFecha(idBarbero, fecha);
    }
    
    @GetMapping("/disponibilidad")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener horas disponibles para un barbero en una fecha")
    public ResponseEntity<List<LocalTime>> getDisponibilidad(
            @RequestParam Long idBarbero,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<LocalTime> disponibilidad = citaService.getDisponibilidad(idBarbero, fecha);
        return ResponseEntity.ok(disponibilidad);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancelar una cita activa")
    public ResponseEntity<CitaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelar(id));
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('BARBERO', 'ADMINISTRADOR', 'SUPERADMIN')")
    @Operation(summary = "Barbero confirma inicio de cita")
    public ResponseEntity<CitaDTO> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.iniciar(id));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('BARBERO', 'ADMINISTRADOR', 'SUPERADMIN')")
    @Operation(summary = "Barbero confirma fin de cita y actualiza promedio del cliente")
    public ResponseEntity<CitaDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.finalizar(id));
    }

    @PatchMapping("/{id}/no-presento")
    @PreAuthorize("hasAnyRole('BARBERO', 'ADMINISTRADOR', 'SUPERADMIN')")
    @Operation(summary = "Barbero marca cliente como no presentado")
    public ResponseEntity<CitaDTO> noPresento(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.marcarNoPresento(id));
    }
}
