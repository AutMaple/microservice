package com.autmaple.config;

import com.autmaple.common.config.JwtTokenRequestInterceptor;
import com.autmaple.common.config.KeycloakJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, KeycloakJwtAuthenticationConverter converter) throws Exception {
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(converter);
        return http.build();
    }

    @Bean
    public KeycloakJwtAuthenticationConverter jwtAuthenticationConverter() {
        return new KeycloakJwtAuthenticationConverter();
    }

    @Bean
    public JwtTokenRequestInterceptor jwtTokenRequestInterceptor() {
        return new JwtTokenRequestInterceptor();
    }
}
