package com.example.backendApocalysus.Seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SecurityConfig {

    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    // 🔹 Seguridad principal
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 🔓 PRE-FLIGHT CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 🔓 RUTAS PÚBLICAS
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()

                        // 🛒 CARRITO - REQUIERE AUTENTICACIÓN (cualquier usuario logueado)
                        .requestMatchers("/api/carrito/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")

                        // 📦 PEDIDOS
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/nuevo").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/pedidos/historial").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/pedidos/metodospago").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/pedidos").hasRole("ADMINISTRADOR") // ver TODOS los pedidos

                        // 👤 USUARIOS
                        .requestMatchers("/api/usuarios/me").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/direcciones/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/listar").hasRole("ADMINISTRADOR")

                        // 📝 PRODUCTOS (ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/productos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMINISTRADOR")

                        // 🏷️ CATEGORÍAS (ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMINISTRADOR")

                        // 👑 ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")

                        // 🔐 TODO LO DEMÁS REQUIERE AUTENTICACIÓN
                        .anyRequest().authenticated()
                );

        // Agregar filtro JWT
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔹 AuthenticationManager para login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 🔹 Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
