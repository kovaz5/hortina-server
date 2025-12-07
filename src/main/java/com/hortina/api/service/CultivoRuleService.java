package com.hortina.api.service;

import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.PlantProfile;
import com.hortina.api.domain.Tarea;
import com.hortina.api.domain.enums.TipoOrigen;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CultivoRuleService {

    public List<Tarea> generateRulesBasedTasks(Cultivo cultivo, PlantProfile profile) {
        List<Tarea> tareas = new ArrayList<>();

        LocalDate baseDate = cultivo.getFecha_plantacion() != null
                ? cultivo.getFecha_plantacion()
                : LocalDate.now();

        boolean isSeed = cultivo.getEstado() != null
                && cultivo.getEstado().name().equalsIgnoreCase("semilla");

        tareas.addAll(generateWateringTasks(cultivo, profile, baseDate, isSeed));

        if (!isSeed) {
            tareas.addAll(generateFertilizingTasks(cultivo, profile, baseDate));
            tareas.addAll(generatePruningTasks(cultivo, profile, baseDate));
        }

        tareas.addAll(generateSunlightTask(cultivo, profile, baseDate));
        tareas.addAll(generateHarvestTask(cultivo, profile, baseDate));

        return tareas;
    }

    private List<Tarea> generateWateringTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate,
            boolean isSeed) {
        List<Tarea> out = new ArrayList<>();
        int frecuencia = mapWateringToDays(profile.getWatering());

        if (isSeed) {
            frecuencia = Math.max(1, frecuencia - 2);
        }

        Tarea t = new Tarea();
        t.setCultivo(cultivo);
        t.setNombreTarea("Regar");
        t.setDescripcion("Riego según perfil ("
                + profile.getWatering()
                + ")"
                + (isSeed ? " - Etapa de semilla" : ""));
        t.setFechaSugerida(baseDate.plusDays(frecuencia));
        t.setTipo_origen(TipoOrigen.automática_api);
        t.setCompletada(false);
        t.setCreated_at(LocalDate.now());
        t.setRecurrente(true);
        t.setFrecuenciaDias(frecuencia);
        out.add(t);

        return out;
    }

    private List<Tarea> generateFertilizingTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> out = new ArrayList<>();
        int frecuencia = inferFertilizingFrequency(profile);

        Tarea t = new Tarea();
        t.setCultivo(cultivo);
        t.setNombreTarea("Fertilizar");
        t.setDescripcion("Fertilización automática (nivel: " + profile.getCareLevel() + ")");
        t.setFechaSugerida(baseDate.plusDays(frecuencia));
        t.setTipo_origen(TipoOrigen.automática_api);
        t.setCompletada(false);
        t.setCreated_at(LocalDate.now());
        t.setRecurrente(true);
        t.setFrecuenciaDias(frecuencia);
        out.add(t);

        return out;
    }

    private List<Tarea> generatePruningTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> out = new ArrayList<>();
        String cycle = profile.getLifeCycle() == null ? "" : profile.getLifeCycle().toLowerCase();

        if (!cycle.contains("perennial"))
            return out;

        int frecuencia = 60;

        Tarea t = new Tarea();
        t.setCultivo(cultivo);
        t.setNombreTarea("Poda de mantenimiento");
        t.setDescripcion("Poda periódica (planta perenne)");
        t.setFechaSugerida(baseDate.plusDays(frecuencia));
        t.setTipo_origen(TipoOrigen.automática_api);
        t.setCompletada(false);
        t.setCreated_at(LocalDate.now());
        t.setRecurrente(true);
        t.setFrecuenciaDias(frecuencia);
        out.add(t);

        return out;
    }

    private List<Tarea> generateSunlightTask(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> out = new ArrayList<>();
        String sun = profile.getSunlight();
        if (sun == null || sun.isBlank())
            return out;

        String recomendacion = "Revisa la exposición del cultivo.";
        String s = sun.toLowerCase();
        if (s.contains("full"))
            recomendacion = "Ubica el cultivo con al menos 6h de sol directo.";
        else if (s.contains("partial"))
            recomendacion = "Evita sol directo todo el día; sol parcial recomendado.";
        else if (s.contains("shade"))
            recomendacion = "Lugar luminoso sin sol directo.";

        Tarea t = new Tarea();
        t.setCultivo(cultivo);
        t.setNombreTarea("Comprobar exposición solar");
        t.setDescripcion(recomendacion);
        t.setFechaSugerida(baseDate.plusDays(2));
        t.setTipo_origen(TipoOrigen.automática_api);
        t.setCompletada(false);
        t.setCreated_at(LocalDate.now());
        t.setRecurrente(false);
        t.setFrecuenciaDias(0);
        out.add(t);

        return out;
    }

    private List<Tarea> generateHarvestTask(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> out = new ArrayList<>();
        String cycle = profile.getLifeCycle() == null ? "" : profile.getLifeCycle().toLowerCase();

        int dias;
        if (cycle.contains("annual"))
            dias = 90;
        else if (cycle.contains("biennial"))
            dias = 180;
        else if (cycle.contains("perennial"))
            dias = 365;
        else
            dias = 120;

        int retrasoSemilla = 0;
        if (cultivo.getEstado() != null && cultivo.getEstado().name().equalsIgnoreCase("semilla")) {
            retrasoSemilla = 30;
        }

        LocalDate fechaCosecha = baseDate.plusDays(retrasoSemilla + dias);

        cultivo.setFecha_estimada_cosecha(fechaCosecha);

        Tarea t = new Tarea();
        t.setCultivo(cultivo);
        t.setNombreTarea("Cosecha estimada");
        t.setDescripcion(
                "Cosecha estimada según ciclo (" + profile.getLifeCycle() + ")," +
                        (retrasoSemilla > 0 ? " ajustado por etapa de semilla" : ""));
        t.setFechaSugerida(fechaCosecha);
        t.setTipo_origen(TipoOrigen.automática_api);
        t.setCompletada(false);
        t.setCreated_at(LocalDate.now());
        t.setRecurrente(false);
        t.setFrecuenciaDias(0);

        out.add(t);

        return out;
    }

    private int mapWateringToDays(String watering) {
        if (watering == null)
            return 4;
        String w = watering.toLowerCase();
        if (w.contains("moist") || w.contains("frequent") || w.contains("very"))
            return 2;
        if (w.contains("average") || w.contains("moderate"))
            return 4;
        if (w.contains("low") || w.contains("dry"))
            return 7;
        return 4;
    }

    private int inferFertilizingFrequency(PlantProfile profile) {
        if (profile.getCareLevel() == null)
            return 45;
        String care = profile.getCareLevel().toLowerCase();
        if (care.contains("high"))
            return 15;
        if (care.contains("medium") || care.contains("moderate"))
            return 30;
        return 45;
    }
}
