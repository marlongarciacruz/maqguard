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
                                                .requestMatchers("/", "/login", "/css/**", "/js/**", "/assets/**")
                                                .permitAll()
                                                .requestMatchers("/nuevo", "/guardar", "/editar/**", "/eliminar/**")
                                                .hasRole("ADMIN")
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
                                                .clearAuthentication(true) // 🔥 IMPORTANTE
                                                .permitAll())

                                .sessionManagement(session -> session
                                                .invalidSessionUrl("/login?expired"))

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.sendRedirect("/inicio");
                                                }))

                                .headers(headers -> headers
                                                .cacheControl(cache -> {
                                                }));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}