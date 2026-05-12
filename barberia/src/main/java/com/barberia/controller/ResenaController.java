package com.barberia.controller;

import com.barberia.dto.ResenaDTO;
import com.barberia.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/resenas")
@Tag(name = "Resenas", description = "Gestion de resenas de clientes")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las resenas")
    public List<ResenaDTO> getAll() {
        return resenaService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener resena por ID")
    public ResponseEntity<ResenaDTO> getById(@PathVariable Long id) {
        ResenaDTO dto = resenaService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Crear resena para una cita finalizada")
    public ResenaDTO create(@Valid @RequestBody ResenaDTO dto) {
        return resenaService.save(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Eliminar resena")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resenaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barbero/{idBarbero}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: resenas de un barbero")
    public List<ResenaDTO> getByBarbero(@PathVariable Long idBarbero) {
        return resenaService.findByBarbero(idBarbero);
    }
}
