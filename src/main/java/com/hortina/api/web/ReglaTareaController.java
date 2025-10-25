package com.hortina.api.web;

import com.hortina.api.domain.ReglaTarea;
import com.hortina.api.repo.ReglaTareaRepository;
import com.hortina.api.web.dto.ReglaTareaDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reglas")
public class ReglaTareaController {
    private final ReglaTareaRepository repo;

    public ReglaTareaController(ReglaTareaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ReglaTarea> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ReglaTarea get(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public ReglaTarea create(@RequestBody ReglaTareaDTO dto) {
        ReglaTarea r = new ReglaTarea(null, dto.tipo_cultivo(), dto.accion(), dto.frecuencia_dias(),
                dto.condicion_meteo(), dto.activo() != null ? dto.activo() : true);
        return repo.save(r);
    }

    @PutMapping("/{id}")
    public ReglaTarea update(@PathVariable Integer id, @RequestBody ReglaTareaDTO dto) {
        ReglaTarea r = repo.findById(id).orElseThrow();
        r.setTipo_cultivo(dto.tipo_cultivo());
        r.setAccion(dto.accion());
        r.setFrecuencia_dias(dto.frecuencia_dias());
        r.setCondicion_meteo(dto.condicion_meteo());
        if (dto.activo() != null)
            r.setActivo(dto.activo());
        return repo.save(r);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}