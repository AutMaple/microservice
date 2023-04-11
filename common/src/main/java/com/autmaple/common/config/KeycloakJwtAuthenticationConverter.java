package com.autmaple.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link KeycloakJwtAuthenticationConverter} 用于提取 keycloak token 中的角色信息，并为每一个角色添加 `ROLE_` 前缀。
 * {@link KeycloakJwtAuthenticationConverter} 提取了 keycloak realm 以及 resource(client) 中配置的角色信息。
 */
@Slf4j
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
        Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim("resource_access");
        List<String> resourceRoles = resourceAccess.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<String> realmRoles = realmAccess.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<SimpleGrantedAuthority> authorities = Stream.of(resourceRoles, realmRoles)
                .flatMap(Collection::stream)
                .distinct()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
