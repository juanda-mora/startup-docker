package com.usta.startup.models.services;
import com.usta.startup.entities.PostulacionEntity;
import com.usta.startup.entities.RolEntity;
import com.usta.startup.models.DAO.PostulacionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostulacionServiceImplement implements PostulacionService {

    @Autowired
    private PostulacionDAO postulacionDAO;

    @Override
    @Transactional(readOnly = true)
    public List<PostulacionEntity> findAll() {

        return (List<PostulacionEntity>) postulacionDAO.findAll();

    }

    @Override
    @Transactional
    public void save(PostulacionEntity postulacion) {

        postulacionDAO.save(postulacion);
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionEntity findById(Long id) {

        return postulacionDAO.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        postulacionDAO.deleteById(id);
    }

    @Override
    @Transactional
    public PostulacionEntity actualizarPostulacion(PostulacionEntity postulacion) {
        return postulacionDAO.save(postulacion);
    }

    @Override
    @Transactional(readOnly = true)
    public PostulacionEntity viewDetail(Long id) {
        return postulacionDAO.viewDetail(id);
    }
}
