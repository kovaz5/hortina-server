package com.hortina.api.web.dto;

public record VariedadDTO(
        Integer id_variedad,
        Integer id_cultivo,
        String nombre,
        String descripcion) {
}
