package com.usta.startup.models.services;

import com.usta.startup.entities.MentoriaEntity;
import com.usta.startup.repository.MentoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentoriaServiceImplement implements MentoriaService {

    @Autowired
    private MentoriaRepository mentoriaRepository;

    @Override
    public List<MentoriaEntity> findAll() {
        return mentoriaRepository.findAll();
    }

    @Override
    public void save(MentoriaEntity sesionMentoria) {
        mentoriaRepository.save(sesionMentoria); // <- Este es el método clave
    }

    @Override
    public MentoriaEntity findById(Long id) {
        return mentoriaRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        mentoriaRepository.deleteById(id);
    }

    @Override
    public MentoriaEntity actualizarSesionMentoria(MentoriaEntity sesionMentoria) {
        return mentoriaRepository.save(sesionMentoria); // update
    }

    @Override
    public MentoriaEntity viewDetail(Long id) {
        return mentoriaRepository.findById(id).orElse(null);
    }

    @Override
    public List<MentoriaEntity> obtenerMentoriasDisponibles() {
        return mentoriaRepository.findByReservadaFalse();
    }

    @Override
    public List<MentoriaEntity> findByReservadaFalse() {
        return mentoriaRepository.findByReservadaFalse();
    }

    @Override
    public List<MentoriaEntity> findByEmprendedorId(Long id) {
        return mentoriaRepository.findByEmprendedor_Id(id);
    }
}