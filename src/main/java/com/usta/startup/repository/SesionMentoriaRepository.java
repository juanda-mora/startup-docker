package com.usta.startup.repository;


import com.usta.startup.entities.SesionMentoriaEntity;
import com.usta.startup.entities.StartupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SesionMentoriaRepository extends JpaRepository<SesionMentoriaEntity, Long> {
    List<SesionMentoriaEntity> findByUsuarioId(Long usuarioId);
    List<SesionMentoriaEntity> findByReservadaFalse();
    List<SesionMentoriaEntity> findByStartupAndReservadaTrue(StartupEntity startup);
    long countByStartupId(Long startupId);

}
