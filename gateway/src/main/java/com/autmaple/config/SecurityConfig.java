package com.autmaple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return httpSecurity.build();
    }

    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder() {
        // 默认提供的 SupplierReactiveJwtDecoder 不支持 ES256 加密算法
        // 而 Keycloak 提供的 jwt 使用的是 ES256 加密算法进行加密
        // 因此需要为 Keycloak 配置一个 ReactiveJwtDecoder.
        return NimbusReactiveJwtDecoder.withJwkSetUri("http://localhost:10000/realms/ostock/protocol/openid-connect/certs")
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
    }
}
