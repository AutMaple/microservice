package com.autmaple.config;

import com.autmaple.common.config.KeycloakJwtAuthenticationConverter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwt -> Mono.fromSupplier(() -> new KeycloakJwtAuthenticationConverter().convert(jwt)));
        return httpSecurity.build();
    }

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        // todo need to modify
        return null;
    }

    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder() {
        // todo add a ReactiveJwtDecoder
        return null;
    }
}
