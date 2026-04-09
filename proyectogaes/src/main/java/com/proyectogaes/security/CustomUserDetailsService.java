package com.proyectogaes.security;

import com.proyectogaes.entity.Usuario;
import com.proyectogaes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Este es el método que Spring Security llama automáticamente cuando alguien
    // intenta hacer login
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("===> Buscando usuario: " + username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        System.out.println("===> Usuario encontrado: " + usuario.getUsername());
        System.out.println("===> Password en BD: " + usuario.getPassword());
        System.out.println("===> Estado: " + usuario.getEstado());
        System.out.println("===> Rol: " + usuario.getRol());

        String rolNombre = "ROLE_" + usuario.getRol().getDescripcion_rol().toUpperCase();
        System.out.println("===> Rol asignado: " + rolNombre);

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEstado().equals("Activo"),
                true, true, true,
                List.of(new SimpleGrantedAuthority(rolNombre)));
    }
}