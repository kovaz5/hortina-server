package com.hortina.api.web.dto;

public record ReglaTareaDTO(
        Integer id_regla,
        String tipo_cultivo,
        String accion,
        Integer frecuencia_dias,
        String condicion_meteo,
        Boolean activo) {
}
