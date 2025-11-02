package com.hortina.api.web.dto;

import java.util.List;

public record CultivoDetalleDTO(CultivoDTO cultivo, List<TareaDTO> tareas) {
}
