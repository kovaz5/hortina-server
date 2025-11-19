package com.hortina.api.service;

import com.hortina.api.domain.Tarea;
import com.hortina.api.repo.TareaRepository;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskSchedulerService {

    private final TareaRepository tareaRepo;

    public TaskSchedulerService(TareaRepository tareaRepo) {
        this.tareaRepo = tareaRepo;
    }

    /**
     * Execútase o scheduler ao iniciar o servidor porque en desarrollo non
     * o teño sempre en execución.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        System.out.println("[Scheduler] Ejecutando actualización  al iniciar el servidor...");
        processDailyTasks();
    }

    /**
     * Ejecuta cada día a las 06:00:
     * - marca tareas con fecha anterior a hoy como completadas (si no están
     * completadas)
     * - genera siguiente ocurrencia de tareas recurrentes si hace falta
     */
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void processDailyTasks() {
        LocalDate hoy = LocalDate.now();

        // 1) Marcar atrasadas como completadas
        List<Tarea> atrasadas = tareaRepo.findByCompletadaFalseAndFechaSugeridaBefore(hoy);
        atrasadas.forEach(t -> t.setCompletada(true));
        tareaRepo.saveAll(atrasadas);

        // 2) Generar siguientes ocurrencias para tareas recurrentes
        // Buscamos tareas recurrentes cuya última fecha <= hoy (o que no tengan
        // siguiente)
        // Estrategia: para cada cultivo y nombre de tarea, coger la última tarea
        // existente y si es recurrente y su fecha <= hoy generar next
        List<Tarea> ultimas = tareaRepo.findDistinctLastByRecurrenteTrue(); // método custom necesario

        int generadas = 0;
        for (Tarea ultima : ultimas) {
            if (ultima == null || ultima.getFrecuenciaDias() == null || ultima.getFrecuenciaDias() <= 0)
                continue;

            LocalDate siguienteFecha = ultima.getFechaSugerida().plusDays(ultima.getFrecuenciaDias());

            // Si ya existe una tarea con esa fecha --> no generar
            boolean existe = tareaRepo.existsByCultivoAndFechaSugeridaAndNombreTarea(
                    ultima.getCultivo(), siguienteFecha, ultima.getNombreTarea());
            if (existe)
                continue;

            // No generar si supera la fecha de cosecha del cultivo
            LocalDate cosecha = ultima.getCultivo().getFecha_estimada_cosecha();
            if (cosecha != null && siguienteFecha.isAfter(cosecha))
                continue;

            Tarea nueva = new Tarea();
            nueva.setCultivo(ultima.getCultivo());
            nueva.setNombreTarea(ultima.getNombreTarea());
            nueva.setDescripcion(ultima.getDescripcion());
            nueva.setFechaSugerida(siguienteFecha);
            nueva.setTipo_origen(ultima.getTipo_origen()); // normalmente automática_api o manual
            nueva.setCompletada(false);
            nueva.setCreated_at(LocalDate.now());
            nueva.setRecurrente(true);
            nueva.setFrecuenciaDias(ultima.getFrecuenciaDias());
            nueva.setRegla(ultima.getRegla());

            tareaRepo.save(nueva);
            generadas++;
        }

        System.out.println("[Scheduler] marcado: " + atrasadas.size() + " | generadas: " + generadas);
    }

    // Método para ejecutar manualmente desde controlador de pruebas
    public void runOnceNowForTesting() {
        processDailyTasks();
    }
}
