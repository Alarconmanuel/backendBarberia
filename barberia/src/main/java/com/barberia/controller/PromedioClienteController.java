package com.barberia.controller;

import com.barberia.dto.PromedioClienteDTO;
import com.barberia.service.PromedioClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/promedios")
@Tag(name = "Promedios", description = "Consulta de promedios de duracion por cliente")
public class PromedioClienteController {

    private final PromedioClienteService promedioService;

    public PromedioClienteController(PromedioClienteService promedioService) {
        this.promedioService = promedioService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los promedios")
    public List<PromedioClienteDTO> getAll() {
        return promedioService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener promedio por ID")
    public ResponseEntity<PromedioClienteDTO> getById(@PathVariable Long id) {
        PromedioClienteDTO dto = promedioService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{idUsuario}/barbero/{idBarbero}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulta nativa 1: promedio de duracion de un cliente con un barbero especifico")
    public ResponseEntity<PromedioClienteDTO> getByUsuarioYBarbero(@PathVariable Long idUsuario,
                                                                    @PathVariable Long idBarbero) {
        PromedioClienteDTO dto = promedioService.findByUsuarioYBarbero(idUsuario, idBarbero);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}
