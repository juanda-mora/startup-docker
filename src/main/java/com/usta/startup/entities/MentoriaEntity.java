package com.usta.startup.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "SesionMentorias")
public class MentoriaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sesion_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "startup_id", nullable = false)
    private StartupEntity startup;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_hora", nullable = false)
    private Date fechaHora;

    @NotNull
    @Column(name = "notas", nullable = false)
    private String notas;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "imagen", length = 200, nullable = false)
    private String imagen;

    @NotNull
    @Column(name = "reservada")
    private boolean reservada = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emprendedor_id")
    private UsuarioEntity emprendedor;
}
