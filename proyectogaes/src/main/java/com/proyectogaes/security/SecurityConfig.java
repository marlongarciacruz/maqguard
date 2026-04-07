package com.proyectogaes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Le aviso a Spring que esta es una clase de configuración
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler; // El que creamos para redireccionar según el rol

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Permito que cualquiera vea el inicio, el login y los archivos estáticos
                        // (CSS, JS, imágenes)
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/assets/**", "/favicon.ico").permitAll()

                        // 2. Las notificaciones solo las ve gente que ya esté logueada
                        .requestMatchers("/notificaciones/**").authenticated()

                        // 3. Pongo filtros por roles: solo los Admin entran a lo suyo y los Técnicos a
                        // lo suyo
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMINISTRADOR")
                        .requestMatchers("/tecnico/**").hasAnyAuthority("ROLE_TECNICO")

                        // 4. Cualquier otra ruta que me haya faltado, por seguridad, pido que estén
                        // logueados
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        // Configuro mi propio formulario de login
                        .loginPage("/login")
                        // Uso el Handler que hicimos antes para que, al entrar, el usuario vaya a su
                        // dashboard correcto
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        // Configuro la salida: mato la sesión, borro las cookies y lo devuelvo al login
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Le aviso en la URL que ya salió
                        .invalidateHttpSession(true) // Destruyo la sesión del servidor
                        .deleteCookies("JSESSIONID") // Borro el rastro del navegador
                        .permitAll());

        return http.build(); // Construyo toda esta cadena de filtros
    }

    // Este Bean es para que Spring sepa cómo encriptar y comparar las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Uso BCrypt, que es el estándar para no guardar claves en texto plano
        return new BCryptPasswordEncoder();
    }
}