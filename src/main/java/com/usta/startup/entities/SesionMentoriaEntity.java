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
public class SesionMentoriaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sesion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "startup_id", nullable = false)
    private StartupEntity startup;

    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private Date fechaHora = new Date();

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "imagen", length = 200, nullable = false)
    private String imagen;

    
    @Column(name = "notas", nullable = false)
    private String notas;

    @NotNull
    @Column(name = "reservada")
    private boolean reservada = false;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

}
