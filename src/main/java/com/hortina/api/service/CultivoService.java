package com.hortina.api.service;

import com.hortina.api.domain.Cultivo;
import com.hortina.api.domain.PlantProfile;
import com.hortina.api.domain.Usuario;
import com.hortina.api.repo.CultivoRepository;
import com.hortina.api.repo.UsuarioRepository;
import com.hortina.api.repo.UbicacionRepository;
import com.hortina.api.web.dto.CultivoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CultivoService {

    private static final Logger log = LoggerFactory.getLogger(CultivoService.class);

    private final CultivoRepository cultivoRepo;
    private final UsuarioRepository usuarioRepo;
    private final UbicacionRepository ubicacionRepo;
    private final PlantProfileService plantProfileService;
    private final TaskGenerationService taskGenerationService;

    public CultivoService(CultivoRepository cultivoRepo,
            UsuarioRepository usuarioRepo,
            UbicacionRepository ubicacionRepo,
            PlantProfileService plantProfileService,
            TaskGenerationService taskGenerationService) {
        this.cultivoRepo = cultivoRepo;
        this.usuarioRepo = usuarioRepo;
        this.ubicacionRepo = ubicacionRepo;
        this.plantProfileService = plantProfileService;
        this.taskGenerationService = taskGenerationService;
    }

    @Transactional
    public Cultivo crearCultivoFromDto(CultivoDTO dto) throws Exception {
        Cultivo cultivo = new Cultivo();

        if (dto.id_usuario() != null) {
            Usuario usuario = usuarioRepo.findById(dto.id_usuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no existe: " + dto.id_usuario()));
            cultivo.setUsuario(usuario);
        }

        if (dto.id_ubicacion() != null) {
            ubicacionRepo.findById(dto.id_ubicacion())
                    .ifPresentOrElse(
                            cultivo::setUbicacion,
                            () -> log.warn("Ubicación no encontrada: {} — se guarda sin ubicación.",
                                    dto.id_ubicacion()));
        }

        PlantProfile profile = null;
        if (dto.plantExternalId() != null) {
            profile = plantProfileService.fetchProfileByExternalId(dto.plantExternalId());
            cultivo.setPlantProfile(profile);
        }

        String nombre = (dto.nombre() != null && !dto.nombre().isBlank())
                ? dto.nombre()
                : (profile != null ? profile.getCommonName() : "Cultivo sin nombre");
        cultivo.setNombre(nombre);

        String tipo = (dto.tipo() != null && !dto.tipo().isBlank())
                ? dto.tipo()
                : (profile != null && profile.getLifeCycle() != null ? profile.getLifeCycle() : "Desconocido");
        cultivo.setTipo(tipo);

        String imagen = (dto.imagen() != null && !dto.imagen().isBlank())
                ? dto.imagen()
                : (profile != null ? profile.getImageUrl() : null);
        cultivo.setImagen(imagen);
        cultivo.setFecha_plantacion(dto.fecha_plantacion());
        cultivo.setEstado(dto.estado());

        if (dto.fecha_estimada_cosecha() != null) {
            cultivo.setFecha_estimada_cosecha(dto.fecha_estimada_cosecha());
        } else if (dto.fecha_plantacion() != null) {
            int diasEstimados = switch (tipo.toLowerCase()) {
                case "annual" -> 90;
                case "biennial" -> 180;
                case "perennial" -> 240;
                default -> 120;
            };
            cultivo.setFecha_estimada_cosecha(dto.fecha_plantacion().plusDays(diasEstimados));
        }

        Cultivo saved = cultivoRepo.save(cultivo);
        log.info("Cultivo guardado correctamente: {} (ID: {})", saved.getNombre(), saved.getIdCultivo());

        if (saved.getPlantProfile() != null) {
            try {
                taskGenerationService.generateTasksForCultivo(
                        saved.getIdCultivo(),
                        saved.getPlantProfile().getExternalId());
                log.info("Tareas automáticas generadas para cultivo {}", saved.getIdCultivo());
            } catch (Exception e) {
                log.error("No se pudieron generar tareas automáticas para cultivo {}: {}",
                        saved.getIdCultivo(), e.getMessage());
            }
        } else {
            log.warn("Cultivo {} guardado sin perfil de planta. No se generaron tareas automáticas.",
                    saved.getIdCultivo());
        }

        return saved;
    }

    @Transactional
    public Cultivo updateCultivoFromDto(Integer id, CultivoDTO dto) throws Exception {
        Cultivo cultivo = cultivoRepo.findById(id)
                .orElseThrow(() -> new Exception("Cultivo no encontrado: " + id));

        if (dto.nombre() != null)
            cultivo.setNombre(dto.nombre());
        if (dto.tipo() != null)
            cultivo.setTipo(dto.tipo());
        if (dto.estado() != null)
            cultivo.setEstado(dto.estado());
        if (dto.fecha_plantacion() != null)
            cultivo.setFecha_plantacion(dto.fecha_plantacion());
        if (dto.imagen() != null)
            cultivo.setImagen(dto.imagen());
        if (dto.fecha_estimada_cosecha() != null)
            cultivo.setFecha_estimada_cosecha(dto.fecha_estimada_cosecha());

        return cultivoRepo.save(cultivo);
    }

    public List<Cultivo> listAll() {
        return cultivoRepo.findAll();
    }

    public Cultivo getById(Integer id) throws Exception {
        return cultivoRepo.findById(id)
                .orElseThrow(() -> new Exception("Cultivo no encontrado: " + id));
    }

    public void deleteById(Integer id) throws Exception {
        if (!cultivoRepo.existsById(id)) {
            throw new Exception("Cultivo no encontrado: " + id);
        }
        cultivoRepo.deleteById(id);
    }
}
