package com.barberia.controller;

import com.barberia.dto.NotificacionDTO;
import com.barberia.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Registro de notificaciones WhatsApp")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR', 'BARBERO')")
    @Operation(summary = "Listar todas las notificaciones")
    public List<NotificacionDTO> getAll() {
        return notificacionService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR', 'BARBERO')")
    @Operation(summary = "Obtener notificacion por ID")
    public ResponseEntity<NotificacionDTO> getById(@PathVariable Long id) {
        NotificacionDTO dto = notificacionService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Registrar notificacion (simula envio WhatsApp via Twilio en produccion)")
    public NotificacionDTO create(@Valid @RequestBody NotificacionDTO dto) {
        return notificacionService.save(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR')")
    @Operation(summary = "Eliminar notificacion")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cita/{idCita}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMINISTRADOR', 'BARBERO')")
    @Operation(summary = "Consulta nativa 1: notificaciones de una cita")
    public List<NotificacionDTO> getByCita(@PathVariable Long idCita) {
        return notificacionService.findByCita(idCita);
    }
}
