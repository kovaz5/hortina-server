package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.domain.enums.TipoOrigen;
import com.hortina.api.repo.*;
import com.hortina.api.web.dto.TareaDTO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {
    private final TareaRepository repo;
    private final CultivoRepository cultivos;
    private final ReglaTareaRepository reglas;

    public TareaController(TareaRepository repo, CultivoRepository cultivos, ReglaTareaRepository reglas) {
        this.repo = repo;
        this.cultivos = cultivos;
        this.reglas = reglas;
    }

    @GetMapping
    public List<Tarea> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Tarea get(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @GetMapping("/cultivo/{cultivoId}")
    public List<Tarea> listByCultivo(@PathVariable Integer cultivoId) {
        System.out.println("Consultando tareas del cultivo ID: " + cultivoId);
        List<Tarea> tareas = repo.findByCultivo_IdCultivo(cultivoId);
        System.out.println("Se encontraron " + tareas.size() + " tareas.");
        return tareas;
    }

    @PostMapping
    public Tarea create(@RequestBody TareaDTO dto) {
        Cultivo c = cultivos.findById(dto.id_cultivo()).orElseThrow();
        ReglaTarea r = (dto.id_regla() != null) ? reglas.findById(dto.id_regla()).orElse(null) : null;
        Tarea t = new Tarea(null, c, dto.nombre_tarea(), dto.descripcion(), dto.fecha_sugerida(),
                dto.completada() != null ? dto.completada() : false,
                dto.tipo_origen() != null ? TipoOrigen.valueOf(dto.tipo_origen()) : TipoOrigen.manual,
                r, dto.created_at());
        return repo.save(t);
    }

    @PutMapping("/{id}")
    public Tarea update(@PathVariable Integer id, @RequestBody TareaDTO dto) {
        Tarea t = repo.findById(id).orElseThrow();
        if (dto.id_cultivo() != null)
            t.setCultivo(cultivos.findById(dto.id_cultivo()).orElseThrow());
        if (dto.nombre_tarea() != null)
            t.setNombre_tarea(dto.nombre_tarea());
        t.setDescripcion(dto.descripcion());
        t.setFechaSugerida(dto.fecha_sugerida());
        if (dto.completada() != null)
            t.setCompletada(dto.completada());
        if (dto.tipo_origen() != null)
            t.setTipo_origen(TipoOrigen.valueOf(dto.tipo_origen()));
        if (dto.id_regla() != null)
            t.setRegla(reglas.findById(dto.id_regla()).orElse(null));
        return repo.save(t);
    }

    @PatchMapping("/{id}/estado")
    public Tarea actualizarEstado(@PathVariable Integer id, @RequestParam boolean completada) {
        Tarea tarea = repo.findById(id).orElseThrow();
        tarea.setCompletada(completada);
        return repo.save(tarea);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }

    @GetMapping("/proximas")
    public List<Tarea> listProximas(@RequestParam(required = false, defaultValue = "2") int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        return repo.findByCompletadaFalseAndFechaSugeridaBetween(hoy, limite);
    }

}
