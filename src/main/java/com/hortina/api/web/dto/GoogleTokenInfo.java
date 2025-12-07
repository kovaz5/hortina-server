package com.hortina.api.web.dto;

public record GoogleTokenInfo(
        String email,
        String name,
        String aud,
        String picture,
        String iss) {
}
