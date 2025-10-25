package com.hortina.api.repo;

import com.hortina.api.domain.Meteorologia;
import com.hortina.api.domain.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MeteorologiaRepository extends JpaRepository<Meteorologia, Integer> {
    List<Meteorologia> findByUbicacionAndFechaBetween(Ubicacion ubicacion, LocalDate desde, LocalDate hasta);
}
