package com.usta.startup.repository;

import com.usta.startup.entities.PostulacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostulacionRepository extends JpaRepository<PostulacionEntity, Long> {
    long countByStartupId(Long startupId);
    long countByConvocatoriaId(Long convocatoriaId);

}
