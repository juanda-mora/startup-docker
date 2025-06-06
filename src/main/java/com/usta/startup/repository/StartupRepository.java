package com.usta.startup.repository;

import com.usta.startup.entities.StartupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StartupRepository extends JpaRepository<StartupEntity, Long> {
    long countById(Long id);

}
