package com.usta.startup.models.DAO;
import com.usta.startup.entities.MentoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentoriaDAO extends JpaRepository<MentoriaEntity, Long> {

    @Query("SELECT e FROM MentoriaEntity e WHERE e.id = ?1")
    public MentoriaEntity viewDetail(Long id);

}
