package com.usta.startup.models.services;

import com.usta.startup.entities.UsuarioEntity;
import com.usta.startup.models.DAO.UsuarioDAO;
import com.usta.startup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImplement implements UsuarioService {
    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public List<UsuarioEntity> findAll() {
        return (List<UsuarioEntity>) usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public UsuarioEntity save(UsuarioEntity usuario) {
        // Solo hasheamos si la contraseña no está ya codificada
        if (!usuario.getPassword().startsWith("{bcrypt}")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioEntity findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UsuarioEntity actualizarUsuario(UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioEntity viewDetail(Long id) {
        return usuarioRepository.findById(id).orElse(null); // Simplificado
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioEntity autenticarUsuario(String correo, String password) {
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioEntity> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

}

