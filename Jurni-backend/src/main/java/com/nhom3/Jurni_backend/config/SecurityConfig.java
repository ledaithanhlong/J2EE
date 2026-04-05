package com.nhom3.Jurni_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/hotels/**").permitAll()
                .requestMatchers("/api/flights/**").permitAll()
                .requestMatchers("/api/cars/**").permitAll()
                .requestMatchers("/api/activities/**").permitAll()
                .requestMatchers("/api/testimonials/**").permitAll()
                .requestMatchers("/api/team/**").permitAll()
                .requestMatchers("/api/gallery/**").permitAll()
                .requestMatchers("/api/career-values/**").permitAll()
                .requestMatchers("/api/payments/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/vouchers/**").permitAll()
                // Everything else requires authentication header (we validate manually via clerkId header)
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
