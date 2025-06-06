package com.usta.startup.models.services;

import com.usta.startup.entities.ConvocatoriaEntity;

import java.util.List;

public interface ConvocatoriaService {
    List<ConvocatoriaEntity> findAll();
    void save(ConvocatoriaEntity convocatoria);
    ConvocatoriaEntity findById(Long id);
    void deleteById(Long id);
    ConvocatoriaEntity actualizarConvocatoria(ConvocatoriaEntity convocatoria);
    ConvocatoriaEntity viewDetail(Long id);
    List<ConvocatoriaEntity> obtenerDisponiblesParaEmprendedor(Long emprendedorId);
    List<ConvocatoriaEntity> obtenerInscritasPorEmprendedor(Long emprendedorId);
}
