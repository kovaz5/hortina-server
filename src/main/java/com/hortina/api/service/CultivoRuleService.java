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

    /**
     * Genera una lista de tareas sugeridas para un cultivo en función
     * de la información del perfil de planta obtenido desde la API externa.
     */
    public List<Tarea> generateRulesBasedTasks(Cultivo cultivo, PlantProfile profile) {

        List<Tarea> tareas = new ArrayList<>();
        LocalDate baseDate = cultivo.getFecha_plantacion() != null
                ? cultivo.getFecha_plantacion()
                : LocalDate.now();

        // 🪴 1. Tareas de riego (según requerimiento de agua)
        tareas.addAll(generateWateringTasks(cultivo, profile, baseDate));

        // ☀️ 2. Tareas de exposición solar
        tareas.addAll(generateSunlightTasks(cultivo, profile, baseDate));

        // 🌾 3. Tareas de fertilización / abono
        tareas.addAll(generateFertilizingTasks(cultivo, profile, baseDate));

        // ✂️ 4. Poda o mantenimiento
        tareas.addAll(generatePruningTasks(cultivo, profile, baseDate));

        // 🌱 5. Cosecha estimada (según ciclo de vida)
        tareas.addAll(generateHarvestTasks(cultivo, profile, baseDate));

        return tareas;
    }

    // ---------- SUBREGLAS ----------

    private List<Tarea> generateWateringTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> tareas = new ArrayList<>();
        int dias = mapWateringToDays(profile.getWatering());
        LocalDate fechaRiego = baseDate.plusDays(dias);

        Tarea riego = new Tarea();
        riego.setCultivo(cultivo);
        riego.setNombre_tarea("Riego");
        riego.setDescripcion("Riego sugerido según perfil (" + profile.getWatering() + ")");
        riego.setFechaSugerida(fechaRiego);
        riego.setTipo_origen(TipoOrigen.automática_api);
        riego.setCompletada(false);
        riego.setCreated_at(LocalDate.now());
        tareas.add(riego);

        return tareas;
    }

    private List<Tarea> generateSunlightTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> tareas = new ArrayList<>();
        String sun = profile.getSunlight();
        if (sun == null || sun.isBlank())
            return tareas;

        String recomendacion = switch (sun.toLowerCase()) {
            case String s when s.contains("full") -> "Ubica el cultivo en una zona con al menos 6h de sol directo.";
            case String s when s.contains("partial") -> "Evita exposición directa todo el día; ideal sol parcial.";
            case String s when s.contains("shade") -> "Coloca en un lugar luminoso pero sin sol directo.";
            default -> "Revisa la ubicación del cultivo según sus necesidades de luz.";
        };

        Tarea luz = new Tarea();
        luz.setCultivo(cultivo);
        luz.setNombre_tarea("Comprobar exposición solar");
        luz.setDescripcion(recomendacion);
        luz.setFechaSugerida(baseDate.plusDays(2));
        luz.setTipo_origen(TipoOrigen.automática_api);
        luz.setCompletada(false);
        luz.setCreated_at(LocalDate.now());
        tareas.add(luz);

        return tareas;
    }

    private List<Tarea> generateFertilizingTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> tareas = new ArrayList<>();

        // Regla simple según nivel de cuidado
        int frecuencia;
        String care = profile.getCareLevel() != null ? profile.getCareLevel().toLowerCase() : "";

        if (care.contains("high"))
            frecuencia = 15;
        else if (care.contains("medium") || care.contains("moderate"))
            frecuencia = 30;
        else
            frecuencia = 45;

        Tarea abono = new Tarea();
        abono.setCultivo(cultivo);
        abono.setNombre_tarea("Fertilizar");
        abono.setDescripcion(
                "Fertilización sugerida cada " + frecuencia + " días según nivel de cuidado (" + care + ")");
        abono.setFechaSugerida(baseDate.plusDays(frecuencia));
        abono.setTipo_origen(TipoOrigen.automática_api);
        abono.setCompletada(false);
        abono.setCreated_at(LocalDate.now());
        tareas.add(abono);

        return tareas;
    }

    private List<Tarea> generatePruningTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> tareas = new ArrayList<>();
        String cycle = profile.getLifeCycle() != null ? profile.getLifeCycle().toLowerCase() : "";

        // solo si la planta es perenne o arbustiva
        if (cycle.contains("perennial")) {
            Tarea poda = new Tarea();
            poda.setCultivo(cultivo);
            poda.setNombre_tarea("Poda de mantenimiento");
            poda.setDescripcion("Revisar y podar ramas secas o dañadas (planta perenne)");
            poda.setFechaSugerida(baseDate.plusDays(60));
            poda.setTipo_origen(TipoOrigen.automática_api);
            poda.setCompletada(false);
            poda.setCreated_at(LocalDate.now());
            tareas.add(poda);
        }

        return tareas;
    }

    private List<Tarea> generateHarvestTasks(Cultivo cultivo, PlantProfile profile, LocalDate baseDate) {
        List<Tarea> tareas = new ArrayList<>();
        String cycle = profile.getLifeCycle() != null ? profile.getLifeCycle().toLowerCase() : "";

        int diasCosecha = switch (cycle) {
            case String s when s.contains("annual") -> 90;
            case String s when s.contains("biennial") -> 180;
            case String s when s.contains("perennial") -> 365;
            default -> 120;
        };

        Tarea cosecha = new Tarea();
        cosecha.setCultivo(cultivo);
        cosecha.setNombre_tarea("Cosecha estimada");
        cosecha.setDescripcion("Fecha estimada de recolección según ciclo de vida (" + cycle + ")");
        cosecha.setFechaSugerida(baseDate.plusDays(diasCosecha));
        cosecha.setTipo_origen(TipoOrigen.automática_api);
        cosecha.setCompletada(false);
        cosecha.setCreated_at(LocalDate.now());
        tareas.add(cosecha);

        return tareas;
    }

    // ---------- Utilidades ----------
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
}
