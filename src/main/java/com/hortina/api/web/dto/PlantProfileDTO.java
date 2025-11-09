package com.hortina.api.web.dto;

public record PlantProfileDTO(
    Integer id,
    Integer externalId,
    String commonName,
    String scientificName,
    String imageUrl
) {}