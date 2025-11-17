package com.hortina.api.web.dto;

import java.time.LocalDate;

public record TokenResponse(String accessToken, String refreshToken, UsuarioResponse usuario) {
}
