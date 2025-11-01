package com.hortina.api.web;

import com.hortina.api.domain.*;
import com.hortina.api.service.CultivoService;
import com.hortina.api.web.dto.CultivoDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cultivos")
public class CultivoController {

    private final CultivoService cultivoService;

    public CultivoController(CultivoService cultivoService) {
        this.cultivoService = cultivoService;
    }

    @GetMapping
    public List<Cultivo> list() {
        return cultivoService.listAll();
    }

    @GetMapping("/{id}")
    public Cultivo get(@PathVariable Integer id) throws Exception {
        return cultivoService.getById(id);
    }

    @PostMapping
    public Cultivo create(@RequestBody CultivoDTO dto) throws Exception {
        return cultivoService.crearCultivoFromDto(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws Exception {
        cultivoService.deleteById(id);
    }

}