package com.hortina.api.web.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TareaDTO(
                Integer id_tarea,
                Integer id_cultivo,
                String nombre_tarea,
                String descripcion,
                @JsonProperty("fecha_sugerida") LocalDate fechaSugerida,
                Boolean completada,
                String tipo_origen,
                Integer id_regla,
                LocalDate created_at,
                Boolean recurrente,
                Integer frecuenciaDias) {
}
