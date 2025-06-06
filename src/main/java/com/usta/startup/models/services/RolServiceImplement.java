package com.usta.startup.models.services;
import com.usta.startup.entities.RolEntity;
import com.usta.startup.models.DAO.RolDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolServiceImplement implements RolService {

    @Autowired
    private RolDAO rolDAO;

    @Override
    @Transactional
    public List<RolEntity> findAll() {

        return (List<RolEntity>) rolDAO.findAll();
    }

    @Override
    @Transactional
    public void save(RolEntity rol) {

        rolDAO.save(rol);
    }

    @Override
    @Transactional
    public RolEntity findById(Long id) {

        return rolDAO.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        rolDAO.deleteById(id);
    }

    @Override
    @Transactional
    public RolEntity actualizarRol(RolEntity rol) {

        return rolDAO.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolEntity viewDetail(Long id) {

        return rolDAO.viewDetail(id);
    }
}
