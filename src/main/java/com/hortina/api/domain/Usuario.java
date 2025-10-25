package com.hortina.api.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private LocalDate fecha_registro;

    @Column(nullable = false, length = 255)
    private String password_hash;

    @PrePersist
    protected void onCreate() {
        if (fecha_registro == null) {
            fecha_registro = LocalDate.now();
        }
    }
}