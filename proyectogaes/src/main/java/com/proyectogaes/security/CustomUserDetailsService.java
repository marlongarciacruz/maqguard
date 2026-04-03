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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Ahora este método SÍ existirá gracias al cambio en la entidad Rol
        String rolNombre = "ROLE_" + usuario.getRol().getDescripcion_rol().toUpperCase();

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEstado().equals("Activo"),
                true, true, true,
                List.of(new SimpleGrantedAuthority(rolNombre))
        );
    }
}