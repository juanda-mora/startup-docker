package com.usta.startup.models.services;

import com.usta.startup.entities.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<UsuarioEntity> findAll();

    UsuarioEntity save(UsuarioEntity usuario); // Cambiado para retornar la entidad guardada

    UsuarioEntity findById(Long id);

    void deleteById(Long id);

    UsuarioEntity actualizarUsuario(UsuarioEntity usuario);

    UsuarioEntity viewDetail(Long id);

    UsuarioEntity autenticarUsuario(String correo, String password);

    Optional<UsuarioEntity> findByCorreo(String correo);

}