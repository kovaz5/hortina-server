package com.hortina.api.web.dto;

import java.time.LocalDate;

public record CultivoDTO(
        Integer id_cultivo,
        Integer id_usuario,
        Integer id_ubicacion,
        String nombre,
        String tipo,
        LocalDate fecha_plantacion,
        String estado,
        String imagen,
        LocalDate fecha_estimada_cosecha) {
}
