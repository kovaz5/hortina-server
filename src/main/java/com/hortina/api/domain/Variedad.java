package com.hortina.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variedades", uniqueConstraints = @UniqueConstraint(columnNames = { "id_cultivo", "nombre" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Variedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_variedad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cultivo")
    private Cultivo cultivo;

    @Column(nullable = false)
    private String nombre;

    @Lob
    private String descripcion;
}