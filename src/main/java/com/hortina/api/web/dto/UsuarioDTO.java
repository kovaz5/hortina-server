package com.hortina.api.web.dto;

import java.time.LocalDate;

public record UsuarioDTO(
        Integer id_usuario,
        String nombre,
        String email,
        String password_hash,
        LocalDate fecha_registro) {
}
