package com.usta.startup.models.DAO;

import com.usta.startup.entities.MentoriaEntity;
import com.usta.startup.entities.SesionMentoriaEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SesionDAO {
    @Query("SELECT e FROM MentoriaEntity e WHERE e.id = ?1")
    public MentoriaEntity viewDetail(Long id);
}
