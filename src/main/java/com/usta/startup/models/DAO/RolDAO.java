package com.usta.startup.models.DAO;

import com.usta.startup.entities.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolDAO extends JpaRepository<RolEntity, Long> {

    @Query("SELECT e FROM RolEntity e WHERE e.id = ?1")
    public RolEntity viewDetail(Long id);
}