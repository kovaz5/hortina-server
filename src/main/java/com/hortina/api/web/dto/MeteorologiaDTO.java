package com.hortina.api.web.dto;

import java.time.LocalDate;

public record MeteorologiaDTO(
        Integer id_registro,
        Integer id_ubicacion,
        LocalDate fecha,
        Double precipitacion_mm,
        Double temperatura_media,
        Double humedad,
        String fuente) {
}
