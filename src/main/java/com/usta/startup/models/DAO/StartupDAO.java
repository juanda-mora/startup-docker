package com.usta.startup.models.DAO;

import com.usta.startup.entities.StartupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupDAO extends JpaRepository<StartupEntity, Long> {

    @Query("SELECT e FROM StartupEntity e WHERE e.id = ?1")
    public StartupEntity viewDetail(Long id);

    List<StartupEntity> findByUsuarioId(Long usuarioId);
}
