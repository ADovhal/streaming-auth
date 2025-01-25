package com.project.streaming_auth.config;

import com.project.streaming_auth.security.JwtAuthenticationFilter;
import com.project.streaming_auth.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(@Lazy JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
                .csrf(csrf -> {
                    csrf.ignoringRequestMatchers("/api/auth/refresh", "/api/auth/test");
                    csrf.disable();
                })  // Отключаем CSRF для определенных маршрутов
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login", "/api/auth/refresh", "/api/auth/test").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                                // .cors(cors -> cors.configurationSource(corsConfigurationSource()));  // Закомментируем эту строку, чтобы отключить CORS
        .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Закомментируем или удалим методы, связанные с CORS
    /*
    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        String[] origins = allowedOrigins.split(",");
        for (String origin : origins) {
            corsConfig.addAllowedOrigin(origin.trim());
        }
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        return corsConfig;
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());
        return source;
    }
    */
}