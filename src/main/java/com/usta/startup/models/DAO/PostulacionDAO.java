package com.usta.startup.models.DAO;

import com.usta.startup.entities.ConvocatoriaEntity;
import com.usta.startup.entities.PostulacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostulacionDAO extends JpaRepository<PostulacionEntity, Long> {

    @Query("SELECT e FROM PostulacionEntity e WHERE e.id = ?1")
    PostulacionEntity viewDetail(Long id);

    @Query("SELECT p.convocatoria FROM PostulacionEntity p WHERE p.startup.usuario.id = :emprendedorId")
    List<ConvocatoriaEntity> findConvocatoriasByEmprendedorId(@Param("emprendedorId") Long emprendedorId);
}