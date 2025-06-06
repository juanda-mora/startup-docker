package com.usta.startup.entities;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "startups")
public class StartupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "startup_id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @Size(max = 100)
    @Column(name = "sector", length = 100)
    private String sector;

    @NotNull
    @Size(max = 255)
    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;


}