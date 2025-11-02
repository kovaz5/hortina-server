package com.hortina.api.service;

import com.hortina.api.domain.ReglaTarea;
import com.hortina.api.domain.Tarea;
import com.hortina.api.repo.ReglaTareaRepository;
import com.hortina.api.repo.TareaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskSchedulerService {

    private final TareaRepository tareaRepo;
    private final ReglaTareaRepository reglaRepo;

    public TaskSchedulerService(TareaRepository tareaRepo, ReglaTareaRepository reglaRepo) {
        this.tareaRepo = tareaRepo;
        this.reglaRepo = reglaRepo;
    }

    /**
     * Revisa tareas y reglas cada día a las 06:00
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void processDailyTasks() {
        System.out.println("[Scheduler] Revisando tareas automáticas...");
        LocalDate hoy = LocalDate.now();

        // Marcar tareas pasadas no completadas como “vencidas” (o completadas)
        List<Tarea> atrasadas = tareaRepo.findByCompletadaFalseAndFechaSugeridaBefore(hoy);
        for (Tarea t : atrasadas) {
            t.setCompletada(true);
            tareaRepo.save(t);
        }

        // Reaplicar reglas periódicas
        List<ReglaTarea> reglasActivas = reglaRepo.findByActivoTrue();
        for (ReglaTarea r : reglasActivas) {
            // Aquí podrías regenerar nuevas tareas si toca según frecuencia_dias
            // Ejemplo: si la última tarea fue hace más de frecuencia_dias días, crear nueva
        }

        System.out.println("[Scheduler] Proceso diario completado (" + atrasadas.size() + " tareas actualizadas)");
    }

    // Método auxiliar público para pruebas manuales (puedes invocarlo desde un
    // controller de pruebas)
    public void runOnceNowForTesting() {
        processDailyTasks();
    }
}
