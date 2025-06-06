package com.usta.startup.models.DAO;

import com.usta.startup.entities.ConvocatoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvocatoriaDAO extends JpaRepository<ConvocatoriaEntity, Long> {

    @Query("SELECT e FROM ConvocatoriaEntity e WHERE e.id = ?1")
    ConvocatoriaEntity viewDetail(Long id);
}
