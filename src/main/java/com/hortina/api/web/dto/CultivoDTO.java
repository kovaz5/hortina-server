package com.hortina.api.web.dto;

import java.time.LocalDate;

import com.hortina.api.domain.enums.CultivoEstado;

public record CultivoDTO(
                Integer id_cultivo,
                Integer id_usuario,
                Integer plantExternalId,
                Integer id_ubicacion,
                String nombre,
                String tipo,
                LocalDate fecha_plantacion,
                CultivoEstado estado,
                String imagen,
                LocalDate fecha_estimada_cosecha) {
}
