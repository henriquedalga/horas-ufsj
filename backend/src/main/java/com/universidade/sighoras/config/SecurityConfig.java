package com.universidade.sighoras.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                return configuration;
            }))
            .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll() // <--- PERMITINDO TODAS AS ROTAS TEMPORARIAMENTE
        )
            // .authorizeHttpRequests(authorize -> authorize
            //     .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Importante para requisições preflight!
            //     .requestMatchers("/check").permitAll()
            //     .requestMatchers("/auth/signin-student").permitAll()
            //     .requestMatchers("/auth/signin-admin").permitAll()
            //     .requestMatchers( "/cadastrar/funcionario").permitAll()
            //     .requestMatchers("/aluno/**").permitAll()
            //     .requestMatchers("/auth/aluno/**").permitAll()
            //     .requestMatchers("/solicitacao/add/**").permitAll()
            //     .requestMatchers(HttpMethod.POST, "/solicitacao/add/arquivo/**").permitAll()

            //    // .requestMatchers("/aluno/solicitacao/extensao").permitAll()
            //     .anyRequest().authenticated()
            // )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

}