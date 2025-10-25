package com.hortina.api.domain;

import com.hortina.api.domain.enums.FuenteMeteo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "meteorologia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meteorologia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_registro;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    @Column(nullable = false)
    private LocalDate fecha;

    private Double precipitacion_mm;
    private Double temperatura_media;
    private Double humedad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuenteMeteo fuente = FuenteMeteo.API;
}
