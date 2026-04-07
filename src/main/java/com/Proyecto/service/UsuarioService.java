package com.Proyecto.service;

import com.Proyecto.domain.Rol;
import com.Proyecto.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listarTodos();
    Optional<Usuario> buscarPorId(Long id);
    void registrar(Usuario usuario);
    void desactivar(Long id);
    void eliminar(Long id);
    List<Rol> listarRoles();
    void guardar(Usuario usuario);
}
