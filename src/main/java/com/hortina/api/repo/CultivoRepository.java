package com.hortina.api.repo;

import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.Usuario;
import com.hortina.api.domain.Ubicacion;
import com.hortina.api.domain.enums.CultivoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CultivoRepository extends JpaRepository<Cultivo, Integer> {
    List<Cultivo> findByUsuario(Usuario u);

    List<Cultivo> findByUsuarioAndEstado(Usuario u, CultivoEstado estado);

    List<Cultivo> findByUbicacion(Ubicacion ubicacion);

    List<Cultivo> findByPlantProfile_ScientificName(String scientificName);

}