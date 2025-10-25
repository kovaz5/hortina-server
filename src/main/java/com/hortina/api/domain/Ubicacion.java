package com.hortina.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ubicaciones", uniqueConstraints = @UniqueConstraint(columnNames = { "id_usuario", "nombre" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ubicacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    private String coordenadas;
    @Lob
    private String descripcion;
}
