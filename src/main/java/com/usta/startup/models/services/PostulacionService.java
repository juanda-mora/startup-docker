package com.usta.startup.models.services;

import com.usta.startup.entities.PostulacionEntity;

import java.util.List;

public interface PostulacionService {
    public List<PostulacionEntity> findAll();
    public void save(PostulacionEntity postulacion);
    public PostulacionEntity findById(Long id);
    public void deleteById(Long id);
    public PostulacionEntity actualizarPostulacion(PostulacionEntity postulacion);
    public PostulacionEntity viewDetail(Long id);
}
