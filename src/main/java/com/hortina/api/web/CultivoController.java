package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.service.CultivoService;
import com.hortina.api.web.dto.CultivoDTO;
import com.hortina.api.web.dto.CultivoDetalleDTO;
import com.hortina.api.web.dto.TareaDTO;
import com.hortina.api.repo.TareaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cultivos")
public class CultivoController {

    private final CultivoService cultivoService;
    private final TareaRepository tareaRepo;

    public CultivoController(CultivoService cultivoService, TareaRepository tareaRepo) {
        this.cultivoService = cultivoService;
        this.tareaRepo = tareaRepo;
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        try {
            return ResponseEntity.ok(cultivoService.listAll());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("No autorizado: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            Cultivo cultivo = cultivoService.getById(id);
            return ResponseEntity.ok(cultivo);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/detalle")
    public CultivoDetalleDTO getCultivoDetalle(@PathVariable Integer id) throws Exception {
        Cultivo cultivo = cultivoService.getById(id);
        List<Tarea> tareas = tareaRepo.findByCultivo_IdCultivo(id);

        CultivoDTO cultivoDto = new CultivoDTO(
                cultivo.getIdCultivo(),
                cultivo.getUsuario() != null ? cultivo.getUsuario().getId_usuario() : null,
                cultivo.getPlantProfile() != null ? cultivo.getPlantProfile().getExternalId() : null,
                cultivo.getUbicacion() != null ? cultivo.getUbicacion().getId_ubicacion() : null,
                cultivo.getNombre(),
                cultivo.getTipo(),
                cultivo.getFecha_plantacion(),
                cultivo.getEstado(),
                cultivo.getImagen(),
                cultivo.getFecha_estimada_cosecha());

        List<TareaDTO> tareaDtos = tareas.stream()
                .map(t -> new TareaDTO(
                        t.getId_tarea(),
                        t.getCultivo().getIdCultivo(),
                        t.getNombreTarea(),
                        t.getDescripcion(),
                        t.getFechaSugerida(),
                        t.getCompletada(),
                        t.getTipo_origen().name(),
                        t.getRegla() != null ? t.getRegla().getId_regla() : null,
                        t.getCreated_at(),
                        t.getRecurrente(),
                        t.getFrecuenciaDias()))
                .toList();

        return new CultivoDetalleDTO(cultivoDto, tareaDtos);
    }

    @PostMapping
    public Cultivo create(@RequestBody CultivoDTO dto) throws Exception {
        return cultivoService.crearCultivoFromDto(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CultivoDTO dto) {
        try {
            return ResponseEntity.ok(cultivoService.updateCultivoFromDto(id, dto));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            cultivoService.deleteById(id);
            return ResponseEntity.ok("Cultivo eliminado correctamente");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Acceso denegado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al eliminar: " + e.getMessage());
        }
    }

}