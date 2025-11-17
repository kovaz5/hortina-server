package com.hortina.api.service;

import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.ReglaTarea;
import com.hortina.api.domain.Tarea;
import com.hortina.api.domain.Usuario;
import com.hortina.api.domain.enums.TipoOrigen;
import com.hortina.api.repo.CultivoRepository;
import com.hortina.api.repo.ReglaTareaRepository;
import com.hortina.api.repo.TareaRepository;
import com.hortina.api.repo.UsuarioRepository;
import com.hortina.api.web.dto.TareaDTO;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository tareaRepo;
    private final UsuarioRepository usuarioRepo;
    private final CultivoRepository cultivoRepo;
    private final ReglaTareaRepository reglaRepo;

    public TareaService(
            TareaRepository tareaRepo,
            UsuarioRepository usuarioRepo,
            CultivoRepository cultivoRepo,
            ReglaTareaRepository reglaRepo) {
        this.tareaRepo = tareaRepo;
        this.usuarioRepo = usuarioRepo;
        this.cultivoRepo = cultivoRepo;
        this.reglaRepo = reglaRepo;
    }

    public List<Tarea> getTareasUsuario() throws Exception {
        Usuario usuario = getUsuarioActual();
        return tareaRepo.findByUsuario(usuario);
    }

    public List<Tarea> getTareasByCultivoId(Integer cultivoId) throws Exception {
        Usuario usuario = getUsuarioActual();
        List<Tarea> tareas = tareaRepo.findByCultivo_IdCultivo(cultivoId);

        if (tareas.isEmpty() || !tareas.get(0).getCultivo().getUsuario().getId_usuario()
                .equals(usuario.getId_usuario())) {
            throw new SecurityException("No tienes permiso para acceder a estas tareas.");
        }

        return tareas;
    }

    @Transactional
    public void deleteById(Integer id) throws Exception {
        Usuario usuario = getUsuarioActual();
        Tarea tarea = tareaRepo.findById(id)
                .orElseThrow(() -> new Exception("Tarea no encontrada: " + id));

        if (!tarea.getCultivo().getUsuario().getId_usuario().equals(usuario.getId_usuario())) {
            throw new SecurityException("No tienes permiso para eliminar esta tarea.");
        }

        tareaRepo.delete(tarea);
    }

    @Transactional
    public Tarea crearTareaManual(TareaDTO dto) throws Exception {
        Usuario usuario = getUsuarioActual();

        Cultivo cultivo = cultivoRepo.findById(dto.id_cultivo())
                .orElseThrow(() -> new IllegalArgumentException("Cultivo no encontrado: " + dto.id_cultivo()));

        if (!cultivo.getUsuario().getId_usuario().equals(usuario.getId_usuario())) {
            throw new SecurityException("No tienes permiso para añadir tareas a este cultivo");
        }

        Tarea tarea = new Tarea();
        tarea.setCultivo(cultivo);
        tarea.setNombreTarea(dto.nombre_tarea());
        tarea.setDescripcion(dto.descripcion());
        tarea.setFechaSugerida(dto.fecha_sugerida());
        tarea.setCompletada(dto.completada() != null ? dto.completada() : false);
        tarea.setTipo_origen(dto.tipo_origen() != null ? TipoOrigen.valueOf(dto.tipo_origen()) : TipoOrigen.manual);
        tarea.setCreated_at(dto.created_at() != null ? dto.created_at() : LocalDate.now());

        // Campos nuevos: recurrente + frecuencia
        tarea.setRecurrente(dto.recurrente() != null ? dto.recurrente() : false);
        tarea.setFrecuenciaDias(dto.frecuenciaDias() != null ? dto.frecuenciaDias() : 0);

        return tareaRepo.save(tarea);
    }

    private Usuario getUsuarioActual() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null)
            throw new IllegalStateException("Usuario no autenticado");

        return usuarioRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public List<Tarea> getTareasProximas(int dias) throws Exception {
        Usuario usuario = getUsuarioActual();
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);

        List<Tarea> proximas = tareaRepo.findByCompletadaFalseAndFechaSugeridaBetween(hoy, limite);

        return proximas.stream()
                .filter(t -> t.getCultivo() != null
                        && t.getCultivo().getUsuario() != null
                        && t.getCultivo().getUsuario().getId_usuario().equals(usuario.getId_usuario()))
                .toList();
    }

}
