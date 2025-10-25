package com.hortina.api.repo;

import com.hortina.api.domain.Variedad;
import com.hortina.api.domain.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VariedadRepository extends JpaRepository<Variedad, Integer> {
    List<Variedad> findByCultivo(Cultivo cultivo);
}
