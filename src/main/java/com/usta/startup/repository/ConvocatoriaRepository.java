package com.usta.startup.repository;

import com.usta.startup.entities.ConvocatoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvocatoriaRepository extends JpaRepository<ConvocatoriaEntity, Long> {
    long countById(Long id);

}
