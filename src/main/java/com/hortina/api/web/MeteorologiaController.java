package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.domain.enums.FuenteMeteo;
import com.hortina.api.repo.*;
import com.hortina.api.web.dto.MeteorologiaDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/meteorologia")
public class MeteorologiaController {
    private final MeteorologiaRepository repo;
    private final UbicacionRepository ubicaciones;

    public MeteorologiaController(MeteorologiaRepository repo, UbicacionRepository ubicaciones) {
        this.repo = repo;
        this.ubicaciones = ubicaciones;
    }

    @GetMapping
    public List<Meteorologia> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Meteorologia get(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Meteorologia create(@RequestBody MeteorologiaDTO dto) {
        Ubicacion u = (dto.id_ubicacion() != null) ? ubicaciones.findById(dto.id_ubicacion()).orElse(null) : null;
        Meteorologia m = new Meteorologia(null, u, dto.fecha(),
                dto.precipitacion_mm(), dto.temperatura_media(), dto.humedad(),
                dto.fuente() != null ? FuenteMeteo.valueOf(dto.fuente()) : FuenteMeteo.API);
        return repo.save(m);
    }

    @PutMapping("/{id}")
    public Meteorologia update(@PathVariable Integer id, @RequestBody MeteorologiaDTO dto) {
        Meteorologia m = repo.findById(id).orElseThrow();
        if (dto.id_ubicacion() != null)
            m.setUbicacion(ubicaciones.findById(dto.id_ubicacion()).orElse(null));
        m.setFecha(dto.fecha());
        m.setPrecipitacion_mm(dto.precipitacion_mm());
        m.setTemperatura_media(dto.temperatura_media());
        m.setHumedad(dto.humedad());
        if (dto.fuente() != null)
            m.setFuente(FuenteMeteo.valueOf(dto.fuente()));
        return repo.save(m);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}
