package com.hortina.api.web.dto;

public record TokenResponse(String accessToken, String refreshToken, UsuarioResponse usuario) {
}
