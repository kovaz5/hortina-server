package com.hortina.api.repo;

import com.hortina.api.domain.PlantProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlantProfileRepository extends JpaRepository<PlantProfile, Integer> {
    Optional<PlantProfile> findByScientificNameIgnoreCase(String scientificName);

    Optional<PlantProfile> findByExternalId(Integer externalId);
}
