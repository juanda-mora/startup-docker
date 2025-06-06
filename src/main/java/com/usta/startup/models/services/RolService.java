package com.usta.startup.models.services;

import com.usta.startup.entities.RolEntity;

import java.util.List;

public interface RolService {
    public List<RolEntity> findAll();
    public void save(RolEntity rol);
    public RolEntity findById(Long id);
    public void deleteById(Long id);
    public RolEntity actualizarRol(RolEntity rol);
    public RolEntity viewDetail(Long id);
}
