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
@Table(name = "Postulaciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"startup_id", "convocatoria_id"})
})
public class PostulacionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postulacion_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "startup_id", nullable = false)
    private StartupEntity startup;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "convocatoria_id", nullable = false)
    private ConvocatoriaEntity convocatoria;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "nombre", length = 20, nullable = false, unique = true)
    private String nombre;

    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Size(max = 250)
    @Column(name = "mensaje", length = 250)
    private String mensaje;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_postulacion", nullable = false, updatable = false)
    private Date fechaPostulacion = new Date();

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "imagen", length = 200, nullable = false)
    private String imagen;

    @Column(name = "estado", length = 10, nullable = false)
    private String estado = "PENDIENTE";
}
