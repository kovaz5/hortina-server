package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.service.CultivoService;
import com.hortina.api.web.dto.CultivoDTO;
import com.hortina.api.web.dto.CultivoDetalleDTO;
import com.hortina.api.web.dto.TareaDTO;
import com.hortina.api.repo.TareaRepository;

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
    public List<Cultivo> list() {
        return cultivoService.listAll();
    }

    @GetMapping("/{id}")
    public Cultivo get(@PathVariable Integer id) throws Exception {
        return cultivoService.getById(id);
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
                        t.getNombre_tarea(),
                        t.getDescripcion(),
                        t.getFechaSugerida(),
                        t.getCompletada(),
                        t.getTipo_origen().name(),
                        t.getRegla() != null ? t.getRegla().getId_regla() : null,
                        t.getCreated_at()))
                .toList();

        return new CultivoDetalleDTO(cultivoDto, tareaDtos);
    }

    @PostMapping
    public Cultivo create(@RequestBody CultivoDTO dto) throws Exception {
        return cultivoService.crearCultivoFromDto(dto);
    }

    @PutMapping("/{id}")
    public Cultivo update(@PathVariable Integer id, @RequestBody CultivoDTO dto) throws Exception {
        return cultivoService.updateCultivoFromDto(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws Exception {
        cultivoService.deleteById(id);
    }

}