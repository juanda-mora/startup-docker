package com.usta.startup.models.DAO;

import com.usta.startup.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioDAO extends JpaRepository<UsuarioEntity, Long> {

    @Query("SELECT e FROM UsuarioEntity e WHERE e.id = ?1")
    public UsuarioEntity viewDetail(Long id);

    Optional<UsuarioEntity> findByCorreo(String correo);
}
