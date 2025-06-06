// UsuarioEntity.java
package com.usta.startup.entities;

import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Usuarios")
public class UsuarioEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "correo", length = 150, nullable = false, unique = true)
    private String correo;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", referencedColumnName = "rol_id", nullable = false)
    private RolEntity rol;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "imagen", length = 200, nullable = false)
    private String imagen;
}