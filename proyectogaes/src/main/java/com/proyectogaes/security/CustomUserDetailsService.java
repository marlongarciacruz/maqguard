package com.proyectogaes.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.proyectogaes.entity.Usuario;
import com.proyectogaes.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 🔥 convertir rol a formato Spring
        String rolNombre = "ROLE_" + usuario.getRol().getDescripcion().toUpperCase();

        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(rolNombre));

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEstado().equals("Activo"),
                true,
                true,
                true,
                roles);
    }
}