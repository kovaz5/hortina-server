package com.hortina.api.service;

import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.PlantProfile;
import com.hortina.api.domain.Tarea;
import com.hortina.api.repo.CultivoRepository;
import com.hortina.api.repo.TareaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskGenerationService {

    private final Logger log = LoggerFactory.getLogger(TaskGenerationService.class);

    private final PlantProfileService profileService;
    private final CultivoRepository cultivoRepo;
    private final TareaRepository tareaRepo;
    private final CultivoRuleService ruleService;

    public TaskGenerationService(
            PlantProfileService profileService,
            CultivoRepository cultivoRepo,
            TareaRepository tareaRepo,
            CultivoRuleService ruleService) {
        this.profileService = profileService;
        this.cultivoRepo = cultivoRepo;
        this.tareaRepo = tareaRepo;
        this.ruleService = ruleService;
    }

    public void generateTasksForCultivo(Integer cultivoId, Integer plantApiId) throws Exception {
        log.info("Generando tareas automáticas para cultivo {} (apiId {})", cultivoId, plantApiId);

        Cultivo cultivo = cultivoRepo.findById(cultivoId)
                .orElseThrow(() -> new Exception("Cultivo no encontrado: " + cultivoId));

        PlantProfile profile = profileService.fetchProfileByExternalId(plantApiId);

        List<Tarea> tareas = ruleService.generateRulesBasedTasks(cultivo, profile);

        int creadas = 0;
        for (Tarea t : tareas) {
            boolean exists = tareaRepo.existsByCultivoAndFechaSugeridaAndNombreTarea(
                    cultivo, t.getFechaSugerida(), t.getNombreTarea());

            if (!exists) {
                tareaRepo.save(t);
                creadas++;
            }
        }

        log.info("Tareas automáticas insertadas: {} (de {})", creadas, tareas.size());
    }
}
