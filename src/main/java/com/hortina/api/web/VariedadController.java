package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.repo.*;
import com.hortina.api.web.dto.VariedadDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/variedades")
public class VariedadController {
    private final VariedadRepository repo;
    private final CultivoRepository cultivos;

    public VariedadController(VariedadRepository repo, CultivoRepository cultivos) {
        this.repo = repo;
        this.cultivos = cultivos;
    }

    @GetMapping
    public List<Variedad> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Variedad get(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Variedad create(@RequestBody VariedadDTO dto) {
        Cultivo c = cultivos.findById(dto.id_cultivo()).orElseThrow();
        Variedad v = new Variedad(null, c, dto.nombre(), dto.descripcion());
        return repo.save(v);
    }

    @PutMapping("/{id}")
    public Variedad update(@PathVariable Integer id, @RequestBody VariedadDTO dto) {
        Variedad v = repo.findById(id).orElseThrow();
        if (dto.id_cultivo() != null)
            v.setCultivo(cultivos.findById(dto.id_cultivo()).orElseThrow());
        if (dto.nombre() != null)
            v.setNombre(dto.nombre());
        v.setDescripcion(dto.descripcion());
        return repo.save(v);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}
