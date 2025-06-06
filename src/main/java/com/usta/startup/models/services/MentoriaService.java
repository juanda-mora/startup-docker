package com.usta.startup.models.services;

import com.usta.startup.entities.MentoriaEntity;
import java.util.List;

public interface MentoriaService {
    public List<MentoriaEntity> findAll();
    public void save(MentoriaEntity sesionMentoria);
    public MentoriaEntity findById(Long id);
    public void deleteById(Long id);
    public MentoriaEntity actualizarSesionMentoria(MentoriaEntity sesionMentoria);
    public MentoriaEntity viewDetail(Long id);
    public List<MentoriaEntity> obtenerMentoriasDisponibles();
    List<MentoriaEntity> findByReservadaFalse();
    List<MentoriaEntity> findByEmprendedorId(Long id);


}