package com.ticketti.ms_eventos.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ticketti.ms_eventos.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Configuración de seguridad Spring Security para el microservicio ms-eventos.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Swagger & OpenApi públicos
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Actuators públicos para monitoreo
                        .requestMatchers(
                                "/actuator/**"
                        ).permitAll()
                        // Lectura de eventos y búsqueda pública
                        .requestMatchers(HttpMethod.GET, "/api/v0/Eventos/listarEventos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v0/Eventos/buscarEvento/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v0/Eventos/buscar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v0/Eventos/stock/**").permitAll()
                        // Eventos del organizador autenticado
                        .requestMatchers(HttpMethod.GET, "/api/v0/Eventos/mis").authenticated()
                        // Modificación de stock (consumido por ms-carrito internamente con JWT)
                        .requestMatchers(HttpMethod.PUT, "/api/v0/Eventos/actualizarStock/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v0/Eventos/restaurarStock/**").authenticated()
                        // Endpoints de escritura (Creación, modificación y borrado de eventos requieren JWT)
                        .requestMatchers(HttpMethod.POST, "/api/v0/Eventos/crear").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v0/Eventos/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v0/Eventos/{id}/estado").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v0/Eventos/eliminarEvento/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:8222",
                "http://127.0.0.1:5173"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Usuario-Id",
                "X-Rol-Usuario-Id",
                "X-Requested-With"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
