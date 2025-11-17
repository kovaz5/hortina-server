package com.hortina.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hortina.api.domain.enums.TipoOrigen;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tareas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_tarea;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cultivo")
    private Cultivo cultivo;

    @JsonProperty("nombre_tarea")
    @Column(name = "nombre_tarea")
    private String nombreTarea;

    @Lob
    private String descripcion;

    @Column(name = "fecha_sugerida")
    private LocalDate fechaSugerida;

    @Column(nullable = false)
    private Boolean completada = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOrigen tipo_origen = TipoOrigen.manual;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_regla")
    private ReglaTarea regla;

    @Column(nullable = false)
    private LocalDate created_at = LocalDate.now();

    @Column(nullable = false)
    private Boolean recurrente = false;

    @Column(name = "frecuencia_dias", nullable = false)
    private Integer frecuenciaDias = 0;
}
