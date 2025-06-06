package com.usta.startup.models.services;

import com.usta.startup.entities.ConvocatoriaEntity;
import com.usta.startup.models.DAO.ConvocatoriaDAO;
import com.usta.startup.models.DAO.PostulacionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConvocatoriaServiceImplement implements ConvocatoriaService {

    @Autowired
    private ConvocatoriaDAO convocatoriaDAO;

    @Override
    @Transactional(readOnly = true)
    public List<ConvocatoriaEntity> findAll() {
        return convocatoriaDAO.findAll();
    }

    @Override
    @Transactional
    public void save(ConvocatoriaEntity convocatoria) {
        // DEBUG: Verificar que entra aquí
        System.out.println("==== ConvocatoriaServiceImplement.save() recibe: " + convocatoria);
        convocatoriaDAO.save(convocatoria);
        System.out.println("==== ConvocatoriaServiceImplement.save() terminó.");
    }

    @Override
    @Transactional(readOnly = true)
    public ConvocatoriaEntity findById(Long id) {
        return convocatoriaDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        convocatoriaDAO.deleteById(id);
    }

    @Override
    @Transactional
    public ConvocatoriaEntity actualizarConvocatoria(ConvocatoriaEntity convocatoria) {
        return convocatoriaDAO.save(convocatoria);
    }

    @Override
    @Transactional(readOnly = true)
    public ConvocatoriaEntity viewDetail(Long id) {
        return convocatoriaDAO.viewDetail(id);
    }

    @Autowired
    private PostulacionDAO postulacionDAO;

    @Override
    @Transactional(readOnly = true)
    public List<ConvocatoriaEntity> obtenerInscritasPorEmprendedor(Long emprendedorId) {
        return postulacionDAO.findConvocatoriasByEmprendedorId(emprendedorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConvocatoriaEntity> obtenerDisponiblesParaEmprendedor(Long emprendedorId) {
        List<ConvocatoriaEntity> todas = convocatoriaDAO.findAll();
        List<ConvocatoriaEntity> inscritas = obtenerInscritasPorEmprendedor(emprendedorId);

        return todas.stream()
                .filter(c -> !inscritas.contains(c))
                .toList(); // o .collect(Collectors.toList()) en versiones Java anteriores
    }

}
