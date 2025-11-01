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
    @Column(name = "id_cultivo")
    private Integer idCultivo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(optional = true)
    @JoinColumn(name = "plant_profile_id")
    private PlantProfile plantProfile;

    @Column(nullable = false)
    private String nombre;
    private String tipo;

    private LocalDate fecha_plantacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CultivoEstado estado;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    @Lob
    private String imagen;

    private LocalDate fecha_estimada_cosecha;
}
