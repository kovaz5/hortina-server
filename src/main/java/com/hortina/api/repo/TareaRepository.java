package com.hortina.api.repo;

import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.ReglaTarea;
import com.hortina.api.domain.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByCultivoAndCompletadaFalse(Cultivo cultivo);

    List<Tarea> findByCultivo(Cultivo cultivo);

    List<Tarea> findByCultivo_IdCultivo(Integer idCultivo);

    List<Tarea> findByCompletadaFalseAndFechaSugeridaBetween(LocalDate desde, LocalDate hasta);

    List<Tarea> findByCompletadaFalseAndFechaSugeridaBefore(LocalDate antesDe);

    List<Tarea> findByCultivoAndRegla(Cultivo cultivo, ReglaTarea regla);

    @Query("SELECT t FROM Tarea t WHERE t.cultivo.usuario = :usuario")
    List<Tarea> findByUsuario(@Param("usuario") Object usuario);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Tarea t " +
            "WHERE t.cultivo = :cultivo AND t.fechaSugerida = :fecha AND t.nombreTarea = :nombre")
    boolean existsByCultivoAndFechaSugeridaAndNombreTarea(
            @Param("cultivo") Cultivo cultivo,
            @Param("fecha") LocalDate fecha,
            @Param("nombre") String nombre);

    @Query(value = "SELECT * FROM tareas t " +
            "WHERE t.recurrente = 1 AND t.id_tarea IN (" +
            "  SELECT MAX(t2.id_tarea) FROM tareas t2 GROUP BY t2.id_cultivo, t2.nombre_tarea" +
            ")", nativeQuery = true)
    List<Tarea> findDistinctLastByRecurrenteTrue();

}
