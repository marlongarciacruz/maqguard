package com.proyectogaes.security;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login", "/css/**", "/js/**", "/assets/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                                                .requestMatchers("/tecnico/**").hasRole("TECNICO")
                                                .requestMatchers("/inicio").authenticated()
                                                .anyRequest().authenticated())

                                .formLogin(form -> form
                                                .loginPage("/login")
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
                return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}