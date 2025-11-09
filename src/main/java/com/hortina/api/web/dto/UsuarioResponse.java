package com.hortina.api.web.dto;

import java.time.LocalDate;

public record UsuarioResponse(
        Integer id_usuario,
        String nombre,
        String email,
        LocalDate fecha_registro) {
}
