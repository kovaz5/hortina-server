package com.hortina.api.domain;

import com.hortina.api.domain.enums.CultivoEstado;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "cultivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cultivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_cultivo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;
    private String tipo;

    private LocalDate fecha_plantacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CultivoEstado estado = CultivoEstado.semilla;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    @Lob
    private String imagen;

    private LocalDate fecha_estimada_cosecha;
}
