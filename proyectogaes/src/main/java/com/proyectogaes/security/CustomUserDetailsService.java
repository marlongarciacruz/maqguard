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

        // Busco al usuario en la base de datos por su nombre de usuario. Si no existe,
        // lanzo el error de una.
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Le doy formato al rol para que Spring Security lo entienda (le pongo el
        // prefijo ROLE_ y todo en mayúsculas)
        // Ejemplo: Si en la DB dice "administrador", aquí se vuelve
        // "ROLE_ADMINISTRADOR"
        String rolNombre = "ROLE_" + usuario.getRol().getDescripcion_rol().toUpperCase();

        // Retorno un objeto User (de Spring) con la info necesaria para la sesión:
        return new User(
                usuario.getUsername(), // El nombre de usuario
                usuario.getPassword(), // La contraseña (que ya debe estar encriptada en la DB)
                usuario.getEstado().equals("Activo"), // Si el estado no es 'Activo', no lo deja entrar
                true, // La cuenta no ha expirado
                true, // Las credenciales no han expirado
                true, // La cuenta no está bloqueada
                List.of(new SimpleGrantedAuthority(rolNombre)) // Le asigno su rol/permiso
        );
    }
}