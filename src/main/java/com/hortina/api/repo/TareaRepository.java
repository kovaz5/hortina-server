package com.hortina.api.repo;

import com.hortina.api.domain.Tarea;
import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.ReglaTarea;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {
    List<Tarea> findByCultivoAndCompletadaFalse(Cultivo cultivo);

    List<Tarea> findByCultivo(Cultivo cultivo);

    List<Tarea> findByCultivo_IdCultivo(Integer idCultivo);

    List<Tarea> findByCompletadaFalseAndFechaSugeridaBetween(LocalDate desde, LocalDate hasta);

    List<Tarea> findByCompletadaFalseAndFechaSugeridaBefore(LocalDate antesDe);

    List<Tarea> findByCultivoAndRegla(Cultivo cultivo, ReglaTarea regla);
}
