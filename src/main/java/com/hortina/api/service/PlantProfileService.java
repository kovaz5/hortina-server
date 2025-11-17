package com.hortina.api.service;

import com.hortina.api.domain.PlantProfile;
import com.hortina.api.repo.PlantProfileRepository;
import com.hortina.api.web.dto.PlantProfileDTO;
import com.hortina.api.web.dto.PlantSearchDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlantProfileService {

    private final Logger log = LoggerFactory.getLogger(PlantProfileService.class);
    private final WebClient plantApiClient;
    private final PlantProfileRepository profileRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public PlantProfileService(WebClient permapeopleClient, PlantProfileRepository profileRepo) {
        this.plantApiClient = permapeopleClient;
        this.profileRepo = profileRepo;
    }

    public PlantProfile fetchProfileByExternalId(int externalId) throws Exception {
        Optional<PlantProfile> exist = profileRepo.findByExternalId(externalId);
        if (exist.isPresent()) {
            PlantProfile p = exist.get();
            if (p.getLastFetched() != null && p.getLastFetched().isAfter(LocalDateTime.now().minusDays(30))) {
                return p;
            }
        }

        String uri = "/plants/" + externalId;
        log.info("Consultando Permapeople por ID: {}", uri);

        String json = plantApiClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null)
            throw new Exception("Respuesta vacía de Permapeople");

        JsonNode root = mapper.readTree(json);
        String scientific = root.path("scientific_name").asText("");
        String common = root.path("name").asText("");

        String watering = null;
        String sunlight = null;
        String careLevel = null;
        String lifeCycle = null;
        String height = null;
        String edibleParts = null;
        String imageUrl = null;

        JsonNode dataArray = root.path("data");
        if (dataArray.isArray()) {
            for (JsonNode n : dataArray) {
                String key = n.path("key").asText("");
                String value = n.path("value").asText("");
                if (value.isBlank())
                    continue; // ignorar vacíos

                switch (key) {
                    case "Water requirement" -> watering = value;
                    case "Light requirement" -> sunlight = value;
                    case "Growth" -> careLevel = value;
                    case "Life cycle" -> lifeCycle = value;
                    case "Height" -> height = value;
                    case "Edible parts" -> edibleParts = value;
                }
            }
        }

        JsonNode images = root.path("images");
        if (images.has("title")) {
            imageUrl = images.path("title").asText(null);
        } else if (images.has("thumb")) {
            imageUrl = images.path("thumb").asText(null);
        }

        PlantProfile profile = exist.orElseGet(PlantProfile::new);
        profile.setExternalId(externalId);
        profile.setScientificName(scientific.isBlank() ? null : scientific.toLowerCase());
        profile.setCommonName(common.isBlank() ? null : common);
        profile.setWatering(watering);
        profile.setSunlight(sunlight);
        profile.setCareLevel(careLevel);
        profile.setLifeCycle(lifeCycle);
        profile.setHeight(height);
        profile.setEdibleParts(edibleParts);
        profile.setImageUrl(imageUrl);
        profile.setRawJson(json);
        profile.setLastFetched(LocalDateTime.now());

        return profileRepo.save(profile);
    }

    public List<PlantSearchDTO> searchPlants(String query) throws Exception {
        String uri = "/search";
        log.info("Buscando plantas en Permapeople (POST /search): {}", query);

        String json = plantApiClient.post()
                .uri(uri)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{\"q\":\"" + query + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null)
            throw new Exception("Respuesta vacía de Permapeople (búsqueda)");

        JsonNode root = mapper.readTree(json);
        JsonNode results = root.path("plants");

        List<PlantSearchDTO> list = new ArrayList<>();

        if (results.isArray()) {
            for (JsonNode plantNode : results) {
                int externalId = plantNode.path("id").asInt();
                String name = plantNode.path("name").asText();
                String scientific = plantNode.path("scientific_name").asText("");
                String imageUrl = null;

                JsonNode images = plantNode.path("images");
                if (images.has("thumb")) {
                    imageUrl = images.path("thumb").asText(null);
                }

                list.add(new PlantSearchDTO(
                        externalId,
                        name,
                        scientific,
                        imageUrl));
            }
        }

        return list;
    }

    public PlantProfileDTO toDto(PlantProfile p) {
        return new PlantProfileDTO(
                p.getId(),
                p.getExternalId(),
                p.getCommonName(),
                p.getScientificName(),
                p.getImageUrl(),
                p.getWatering(),
                p.getSunlight(),
                p.getCareLevel(),
                p.getLifeCycle(),
                p.getHeight(),
                p.getEdibleParts());
    }

}
