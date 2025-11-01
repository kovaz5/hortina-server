package com.hortina.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "plant_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "external_id", unique = true)
    private Integer externalId;

    @Column(name = "scientific_name", unique = true)
    private String scientificName;

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "watering")
    private String watering;

    @Column(name = "sunlight")
    private String sunlight;

    @Column(name = "care_level")
    private String careLevel;

    @Column(name = "life_cycle")
    private String lifeCycle;

    @Column(name = "height")
    private String height;

    @Column(name = "edible_parts")
    private String edibleParts;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "raw_json", columnDefinition = "LONGTEXT")
    private String rawJson;

    @Column(name = "last_fetched")
    private LocalDateTime lastFetched = LocalDateTime.now();
}