package com.usta.startup.models.services;
import com.usta.startup.entities.RolEntity;
import com.usta.startup.entities.StartupEntity;
import com.usta.startup.models.DAO.StartupDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StartupServiceImplement implements StartupService {

    @Autowired
    private StartupDAO startupDAO;

    @Override
    @Transactional(readOnly = true)
    public List<StartupEntity> findAll() {
        return (List<StartupEntity>) startupDAO.findAll();
    }

    @Override
    @Transactional
    public void save(StartupEntity startup) {
        startupDAO.save(startup);
    }

    @Override
    @Transactional(readOnly = true)
    public StartupEntity findById(Long id) {
        return startupDAO.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        startupDAO.deleteById(id);
    }

    @Override
    @Transactional
    public StartupEntity actualizarStartup(StartupEntity startup) {
        return startupDAO.save(startup);
    }

    @Override
    @Transactional(readOnly = true)
    public StartupEntity viewDetail(Long id) {
        return startupDAO.viewDetail(id);
    }

    @Override
    public List<StartupEntity> findByUsuarioId(Long usuarioId) {
        return startupDAO.findByUsuarioId(usuarioId);
    }
}
