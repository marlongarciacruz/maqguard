package com.proyectogaes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    // Solo declarar el PasswordEncoder como Bean es suficiente.
    // Spring Boot 4 detecta automáticamente el CustomUserDetailsService
    // que tiene @Service y lo conecta con BCrypt sin necesidad de
    // configurar DaoAuthenticationProvider manualmente.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**",
                                "/assets/**", "/favicon.ico").permitAll()
                        .requestMatchers("/notificaciones/**").authenticated()
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMINISTRADOR")
                        .requestMatchers("/tecnico/**").hasAnyAuthority("ROLE_TECNICO")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}