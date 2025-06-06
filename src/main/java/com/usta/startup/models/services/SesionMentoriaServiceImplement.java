package com.usta.startup.models.services;

import com.usta.startup.entities.SesionMentoriaEntity;
import com.usta.startup.entities.StartupEntity;
import com.usta.startup.repository.SesionMentoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SesionMentoriaServiceImplement implements SesionMentoriaService {

    @Autowired
    private SesionMentoriaRepository sesionRepo;
    @Autowired
    private SesionMentoriaRepository sesionMentoriaRepository;

    @Override
    public List<SesionMentoriaEntity> findAll() {
        return sesionRepo.findAll();
    }

    @Override
    public SesionMentoriaEntity findById(Long id) {
        return sesionRepo.findById(id).orElse(null);
    }

    @Override
    public void save(SesionMentoriaEntity sesion) {
        sesionRepo.save(sesion);
    }

    @Override
    public void deleteById(Long id) {
        sesionRepo.deleteById(id);
    }

    @Override
    public void actualizarSesionMentoria(SesionMentoriaEntity sesion) {
        sesionRepo.save(sesion);
    }

    @Override
    public List<SesionMentoriaEntity> findByUsuarioId(Long idUsuario) {
        return sesionRepo.findByUsuarioId(idUsuario);
    }

    @Override
    public List<SesionMentoriaEntity> obtenerSesionesDisponibles() {
        return sesionMentoriaRepository.findByReservadaFalse();
    }

    @Override
    public List<SesionMentoriaEntity> obtenerSesionesReservadasPorStartup(StartupEntity startup) {
        return sesionMentoriaRepository.findByStartupAndReservadaTrue(startup);
    }
}
