package com.usta.startup.entities;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Roles")
public class RolEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "nombre", length = 20, nullable = false, unique = true)
    private String nombre;
}
