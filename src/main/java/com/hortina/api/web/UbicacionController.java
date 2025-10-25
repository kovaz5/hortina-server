package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.repo.*;
import com.hortina.api.web.dto.UbicacionDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {
    private final UbicacionRepository repo;
    private final UsuarioRepository usuarios;

    public UbicacionController(UbicacionRepository repo, UsuarioRepository usuarios) {
        this.repo = repo;
        this.usuarios = usuarios;
    }

    @GetMapping
    public List<Ubicacion> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Ubicacion get(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Ubicacion create(@RequestBody UbicacionDTO dto) {
        Usuario u = usuarios.findById(dto.id_usuario()).orElseThrow();
        Ubicacion x = new Ubicacion(null, u, dto.nombre(), dto.coordenadas(), dto.descripcion());
        return repo.save(x);
    }

    @PutMapping("/{id}")
    public Ubicacion update(@PathVariable Integer id, @RequestBody UbicacionDTO dto) {
        Ubicacion x = repo.findById(id).orElseThrow();
        if (dto.id_usuario() != null) {
            x.setUsuario(usuarios.findById(dto.id_usuario()).orElseThrow());
        }
        x.setNombre(dto.nombre());
        x.setCoordenadas(dto.coordenadas());
        x.setDescripcion(dto.descripcion());
        return repo.save(x);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}
