package com.proyectogaes.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y páginas públicas
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/assets/**").permitAll()
                
                // Restricciones por Rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/tecnico/**").hasRole("TECNICO")
                
                // El dashboard principal requiere estar autenticado
                .requestMatchers("/inicio").authenticated()
                .anyRequest().authenticated())

            .formLogin(form -> form
                .loginPage("/login")
                // Quitamos el defaultSuccessUrl fijo para manejar la redirección por lógica en el controlador
                .defaultSuccessUrl("/inicio", true) 
                .permitAll())

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll())
            
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?expired"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}