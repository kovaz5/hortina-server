package com.hortina.api.repo;

import com.hortina.api.domain.ReglaTarea;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReglaTareaRepository extends JpaRepository<ReglaTarea, Integer> {

    List<ReglaTarea> findByActivoTrue();

    List<ReglaTarea> findByAccionIgnoreCase(String accion);

}