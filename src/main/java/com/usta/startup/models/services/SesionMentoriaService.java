package com.usta.startup.models.services;

import com.usta.startup.entities.SesionMentoriaEntity;
import com.usta.startup.entities.StartupEntity;

import java.util.List;
import java.util.Optional;

public interface SesionMentoriaService {
    List<SesionMentoriaEntity> findAll();
    SesionMentoriaEntity findById(Long id);
    void save(SesionMentoriaEntity sesion);
    void deleteById(Long id);
    void actualizarSesionMentoria(SesionMentoriaEntity sesion);
    List<SesionMentoriaEntity> findByUsuarioId(Long idUsuario);
    List<SesionMentoriaEntity> obtenerSesionesDisponibles();
    List<SesionMentoriaEntity> obtenerSesionesReservadasPorStartup(StartupEntity startup);
}