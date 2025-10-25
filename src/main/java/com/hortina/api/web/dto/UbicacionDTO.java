package com.hortina.api.web.dto;

public record UbicacionDTO(
        Integer id_ubicacion,
        Integer id_usuario,
        String nombre,
        String coordenadas,
        String descripcion) {
}
