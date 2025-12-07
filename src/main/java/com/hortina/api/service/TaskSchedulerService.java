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

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void processDailyTasks() {
        LocalDate hoy = LocalDate.now();

        List<Tarea> atrasadas = tareaRepo.findByCompletadaFalseAndFechaSugeridaBefore(hoy);
        atrasadas.forEach(t -> t.setCompletada(true));
        tareaRepo.saveAll(atrasadas);

        List<Tarea> ultimas = tareaRepo.findDistinctLastByRecurrenteTrue();

        int generadas = 0;
        for (Tarea ultima : ultimas) {

            if (ultima == null || ultima.getFrecuenciaDias() == null || ultima.getFrecuenciaDias() <= 0)
                continue;

            if (!ultima.getCompletada()) {
                continue;
            }

            if (ultima.getCreated_at() != null && ultima.getCreated_at().isEqual(hoy)) {
                continue;
            }

            LocalDate siguienteFecha = ultima.getFechaSugerida().plusDays(ultima.getFrecuenciaDias());

            boolean existe = tareaRepo.existsByCultivoAndFechaSugeridaAndNombreTarea(
                    ultima.getCultivo(), siguienteFecha, ultima.getNombreTarea());
            if (existe)
                continue;

            LocalDate cosecha = ultima.getCultivo().getFecha_estimada_cosecha();
            if (cosecha != null && siguienteFecha.isAfter(cosecha))
                continue;

            Tarea nueva = new Tarea();
            nueva.setCultivo(ultima.getCultivo());
            nueva.setNombreTarea(ultima.getNombreTarea());
            nueva.setDescripcion(ultima.getDescripcion());
            nueva.setFechaSugerida(siguienteFecha);
            nueva.setTipo_origen(ultima.getTipo_origen());
            nueva.setCompletada(false);
            nueva.setCreated_at(hoy);
            nueva.setRecurrente(true);
            nueva.setFrecuenciaDias(ultima.getFrecuenciaDias());
            nueva.setRegla(ultima.getRegla());

            tareaRepo.save(nueva);
            generadas++;
        }

        System.out.println("[Scheduler] marcado: " + atrasadas.size() + " | generadas: " + generadas);
    }

    public void runOnceNowForTesting() {
        processDailyTasks();
    }
}
