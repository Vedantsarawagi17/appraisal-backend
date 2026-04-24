package com.example.Appraisal_New_Setup.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error").permitAll() // Allow login, register, and Spring errors!
                
                // --- ROLE-BASED ACCESS CONTROL (Option 1) ---

                // APPRAISAL ENDPOINTS
                .requestMatchers("/api/appraisals/initiate").hasRole("HR")
                .requestMatchers("/api/appraisals/cycle/**").hasRole("HR")
                .requestMatchers("/api/appraisals/*/hr/**").hasRole("HR")
                
                .requestMatchers("/api/appraisals/*/manager/**").hasRole("MANAGER")
                .requestMatchers("/api/appraisals/*/employee/**").hasAnyRole("EMPLOYEE", "MANAGER") // Managers are employees too

                // TARGET ENDPOINTS
                // List endpoints (by actor) — must be declared BEFORE wildcard patterns
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/targets/employee/**").hasAnyRole("EMPLOYEE", "MANAGER")
                .requestMatchers("/api/targets/manager/**").hasRole("MANAGER")
                // Action endpoints (by target id + actor)
                .requestMatchers("/api/targets/*/manager/**").hasRole("MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/targets/*/manager/**").hasRole("MANAGER")
                .requestMatchers("/api/targets/*/employee/**").hasAnyRole("EMPLOYEE", "MANAGER")

                // Require authentication for all other requests (like GET endpoints)
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ALLOW ALL ORIGINS FOR TESTING TO FIX THE 403 FORBIDDEN ERROR
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
}
