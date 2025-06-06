package com.usta.startup.models.services;

import com.usta.startup.entities.StartupEntity;

import java.util.List;

public interface StartupService {
    List<StartupEntity> findAll();
    void save(StartupEntity startup);
    StartupEntity findById(Long id);
    void deleteById(Long id);
    StartupEntity actualizarStartup(StartupEntity startup);
    StartupEntity viewDetail(Long id);
    List<StartupEntity> findByUsuarioId(Long usuarioId);
}