package com.hortina.api.web.dto;

public record RegistroRequest(
        String nombre,
        String email,
        String password) {
}
