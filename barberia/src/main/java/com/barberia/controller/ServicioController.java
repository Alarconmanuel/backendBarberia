package com.barberia.controller;

import com.barberia.dto.ServicioDTO;
import com.barberia.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios", description = "CRUD y consultas de servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los servicios")
    public List<ServicioDTO> getAll() {
        return servicioService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener servicio por ID")
    public ResponseEntity<ServicioDTO> getById(@PathVariable Long id) {
        ServicioDTO dto = servicioService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Crear servicio")
    public ServicioDTO create(@Valid @RequestBody ServicioDTO dto) {
        return servicioService.save(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Actualizar servicio")
    public ResponseEntity<ServicioDTO> update(@PathVariable Long id, @Valid @RequestBody ServicioDTO dto) {
        dto.setIdServicio(id);
        return ResponseEntity.ok(servicioService.save(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Eliminar servicio")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/duracion/{minutos}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: servicios con duracion menor o igual a X minutos")
    public List<ServicioDTO> getByDuracion(@PathVariable Integer minutos) {
        return servicioService.findByDuracionMenorIgual(minutos);
    }

    @GetMapping("/especialidad/{especialidad}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar servicios por especialidad")
    public ResponseEntity<List<ServicioDTO>> getByEspecialidad(@PathVariable String especialidad) {
        return ResponseEntity.ok(servicioService.findByEspecialidad(especialidad));
    }
}
