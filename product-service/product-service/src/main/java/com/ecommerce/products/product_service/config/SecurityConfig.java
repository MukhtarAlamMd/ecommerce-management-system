package com.ecommerce.products.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        // =========================================================
        // KEYCLOAK JWT + REALM ROLE CONVERTER
        // =========================================================

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRealmRoleConverter()
        );

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())

                // =================================================
                // STATELESS
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // -----------------------------------------
                        // Swagger / Actuator
                        // -----------------------------------------

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()

                        // -----------------------------------------
                        // Product Images
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/uploads/products/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.HEAD,
                                "/uploads/products/**"
                        ).permitAll()

                        // -----------------------------------------
                        // VIEW PRODUCTS
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**"
                        ).permitAll()

                        // -----------------------------------------
                        // CREATE PRODUCT
                        // ADMIN ONLY
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        // -----------------------------------------
                        // UPDATE PRODUCT
                        // ADMIN ONLY
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        // -----------------------------------------
                        // DELETE PRODUCT
                        // ADMIN ONLY
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        // -----------------------------------------
                        // EVERYTHING ELSE
                        // -----------------------------------------

                        .anyRequest().authenticated()
                )

                // =================================================
                // KEYCLOAK RESOURCE SERVER
                // =================================================

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(converter)
                        )
                );

        return http.build();
    }


    // =============================================================
    // KEYCLOAK REALM ROLE CONVERTER
    // =============================================================

    public static class KeycloakRealmRoleConverter
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {

            Map<String, Object> realmAccess =
                    jwt.getClaim("realm_access");

            if (realmAccess == null) {
                return Collections.emptyList();
            }

            Object rolesObject =
                    realmAccess.get("roles");

            if (!(rolesObject instanceof List<?> roles)) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> (String) role)
                    .map(role -> {

                        if (role.startsWith("ROLE_")) {
                            return new SimpleGrantedAuthority(role);
                        }

                        return new SimpleGrantedAuthority(
                                "ROLE_" + role
                        );
                    })
                    .map(authority ->
                            (GrantedAuthority) authority
                    )
                    .toList();
        }
    }
}
