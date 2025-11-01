package com.hortina.api.web;

import com.hortina.api.domain.PlantProfile;
import com.hortina.api.service.PlantProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plants")
public class PlantProfileController {

    private final PlantProfileService profileService;

    public PlantProfileController(PlantProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<?> getById(@PathVariable("externalId") int externalId) {
        try {
            PlantProfile profile = profileService.fetchProfileByExternalId(externalId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No encontrado: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPlants(@RequestParam("query") String query) {
        try {
            return ResponseEntity.ok(profileService.searchAndCachePlants(query));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al buscar plantas: " + e.getMessage());
        }
    }

}
