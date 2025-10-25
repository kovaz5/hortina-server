package com.hortina.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reglas_tareas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReglaTarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_regla;

    private String tipo_cultivo;

    @Column(nullable = false)
    private String accion;

    private Integer frecuencia_dias;

    private String condicion_meteo;

    @Column(nullable = false)
    private Boolean activo = true;
}
