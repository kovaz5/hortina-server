package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.domain.enums.CultivoEstado;
import com.hortina.api.repo.*;
import com.hortina.api.web.dto.CultivoDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cultivos")
public class CultivoController {
    private final CultivoRepository repo;
    private final UsuarioRepository usuarios;
    private final UbicacionRepository ubicaciones;

    public CultivoController(CultivoRepository repo, UsuarioRepository usuarios, UbicacionRepository ubicaciones) {
        this.repo = repo;
        this.usuarios = usuarios;
        this.ubicaciones = ubicaciones;
    }

    @GetMapping
    public List<Cultivo> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Cultivo get(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Cultivo create(@RequestBody CultivoDTO dto) {
        Usuario u = usuarios.findById(dto.id_usuario()).orElseThrow();
        Ubicacion ub = (dto.id_ubicacion() != null) ? ubicaciones.findById(dto.id_ubicacion()).orElse(null) : null;
        Cultivo c = new Cultivo(
                null,
                u,
                dto.nombre(),
                dto.tipo(),
                dto.fecha_plantacion(),
                dto.estado() != null ? CultivoEstado.valueOf(dto.estado()) : CultivoEstado.semilla,
                ub,
                dto.imagen(),
                dto.fecha_estimada_cosecha());
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public Cultivo update(@PathVariable Integer id, @RequestBody CultivoDTO dto) {
        Cultivo c = repo.findById(id).orElseThrow();
        if (dto.id_usuario() != null)
            c.setUsuario(usuarios.findById(dto.id_usuario()).orElseThrow());
        if (dto.id_ubicacion() != null)
            c.setUbicacion(ubicaciones.findById(dto.id_ubicacion()).orElse(null));
        if (dto.nombre() != null)
            c.setNombre(dto.nombre());
        c.setTipo(dto.tipo());
        c.setFecha_plantacion(dto.fecha_plantacion());
        if (dto.estado() != null)
            c.setEstado(CultivoEstado.valueOf(dto.estado()));

        c.setImagen(dto.imagen());
        c.setFecha_estimada_cosecha(dto.fecha_estimada_cosecha());
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}