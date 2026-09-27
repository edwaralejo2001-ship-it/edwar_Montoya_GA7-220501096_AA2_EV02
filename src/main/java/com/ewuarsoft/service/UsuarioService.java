package com.ewuarsoft.service;

import com.ewuarsoft.model.EstadoUsuario;
import com.ewuarsoft.model.RolUsuario;
import com.ewuarsoft.model.Usuario;
import com.ewuarsoft.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public Optional<Usuario> autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Verificación de credenciales (compatible con contraseñas del semillero)
            if (usuario.getPassword().equals(password) && usuario.getEstado() == EstadoUsuario.ACTIVO) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario '" + usuario.getUsername() + "' ya está registrado.");
        }
        if (usuario.getRol() == null) {
            usuario.setRol(RolUsuario.CAJERO);
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado(EstadoUsuario.ACTIVO);
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Usuario usuarioActualizado) {
        Usuario existente = usuarioRepository.findById(usuarioActualizado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioActualizado.getId()));

        existente.setNombreCompleto(usuarioActualizado.getNombreCompleto());
        existente.setEmail(usuarioActualizado.getEmail());
        existente.setRol(usuarioActualizado.getRol());
        existente.setEstado(usuarioActualizado.getEstado());

        // Actualizar contraseña solo si se proporciona una nueva
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().trim().isEmpty()) {
            existente.setPassword(usuarioActualizado.getPassword().trim());
        }

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void cambiarEstado(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            usuario.setEstado(EstadoUsuario.INACTIVO);
        } else {
            usuario.setEstado(EstadoUsuario.ACTIVO);
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public long contarUsuarios() {
        return usuarioRepository.count();
    }
}
