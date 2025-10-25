package com.hortina.api.repo;

import com.hortina.api.domain.Ubicacion;
import com.hortina.api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    List<Ubicacion> findByUsuario(Usuario usuario);
}
