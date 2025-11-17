package com.hortina.api.web;

import com.hortina.api.domain.Tarea;
import com.hortina.api.service.TareaService;
import com.hortina.api.web.dto.TareaDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(tareaService.getTareasUsuario());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cultivo/{cultivoId}")
    public ResponseEntity<?> listByCultivo(@PathVariable Integer cultivoId) {
        try {
            return ResponseEntity.ok(tareaService.getTareasByCultivoId(cultivoId));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Acceso denegado");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/proximas")
    public ResponseEntity<?> listProximas(@RequestParam(required = false, defaultValue = "2") int dias) {
        try {
            return ResponseEntity.ok(tareaService.getTareasProximas(dias));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TareaDTO dto) {
        try {
            Tarea nueva = tareaService.crearTareaManual(dto);
            return ResponseEntity.ok(nueva);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("No tienes permiso para crear tareas en ese cultivo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear tarea: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            tareaService.deleteById(id);
            return ResponseEntity.ok("Tarea eliminada correctamente");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Acceso denegado");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

}
