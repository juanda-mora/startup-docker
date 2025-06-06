package com.usta.startup.repository;

import com.usta.startup.entities.MentoriaEntity;
import com.usta.startup.entities.StartupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MentoriaRepository extends JpaRepository<MentoriaEntity, Long> {

    // Buscar todas las sesiones creadas por un usuario (mentor)
    List<MentoriaEntity> findByUsuarioId(Long usuarioId);

    // Buscar sesiones reservadas asociadas a una startup
    List<MentoriaEntity> findByStartupAndReservadaTrue(StartupEntity startup);

    // Contar sesiones por startup
    long countByStartupId(Long startupId);
    List<MentoriaEntity> findByReservadaFalse();

    List<MentoriaEntity> findByEmprendedor_Id(Long id);
}
