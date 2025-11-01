package com.hortina.api.service;

import com.hortina.api.domain.Tarea;
import com.hortina.api.repo.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {

    private final TareaRepository tareaRepo;

    public TareaService(TareaRepository tareaRepo) {
        this.tareaRepo = tareaRepo;
    }

    public List<Tarea> getTareasByCultivoId(Integer cultivoId) {
        return tareaRepo.findByCultivo_IdCultivo(cultivoId);
    }
}
