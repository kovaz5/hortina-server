package com.hortina.api.web.dto;

public record PlantProfileDTO(
        Integer id,
        Integer externalId,
        String commonName,
        String scientificName,
        String imageUrl,
        String watering,
        String sunlight,
        String careLevel,
        String lifeCycle,
        String height,
        String edibleParts) {
}
