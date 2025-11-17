package com.hortina.api.web.dto;

public record PlantSearchDTO(
        Integer externalId,
        String commonName,
        String scientificName,
        String imageUrl
) {}
