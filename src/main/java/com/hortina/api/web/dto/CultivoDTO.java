package com.hortina.api.web.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hortina.api.domain.enums.CultivoEstado;

public record CultivoDTO(
        @JsonProperty("idCultivo") Integer idCultivo,
        Integer id_usuario,
        Integer plantExternalId,
        String nombre,
        String tipo,
        LocalDate fecha_plantacion,
        CultivoEstado estado,
        String imagen,
        LocalDate fecha_estimada_cosecha) {
}
