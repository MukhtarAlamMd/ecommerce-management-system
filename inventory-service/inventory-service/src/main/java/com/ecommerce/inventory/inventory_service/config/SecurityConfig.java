package com.ecommerce.inventory.inventory_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==========================================
                        // SWAGGER / ACTUATOR
                        // ==========================================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        )
                        .permitAll()

                        // ==========================================
                        // INVENTORY READ
                        // ==========================================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventory",
                                "/api/inventory/**"
                        )
                        .permitAll()

                        // ==========================================
                        // RESERVE / RELEASE
                        // ==========================================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inventory/*/reserve",
                                "/api/inventory/*/release"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SELLER",
                                "CUSTOMER"
                        )

                        // ==========================================
                        // OTHER POST
                        // ==========================================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inventory/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SELLER"
                        )

                        // ==========================================
                        // UPDATE
                        // ==========================================
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/inventory/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SELLER"
                        )

                        // ==========================================
                        // DELETE
                        // ==========================================
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/inventory/**"
                        )
                        .hasRole("ADMIN")

                        // ==========================================
                        // EVERYTHING ELSE
                        // ==========================================
                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }


    // =========================================================
    // KEYCLOAK JWT ROLE CONVERTER
    // =========================================================

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken>
    jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRealmRoleConverter()
        );

        return converter;
    }


    // =========================================================
    // KEYCLOAK REALM ROLE CONVERTER
    // =========================================================

    static class KeycloakRealmRoleConverter
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {

            Collection<GrantedAuthority> authorities =
                    new ArrayList<>();

            Map<String, Object> realmAccess =
                    jwt.getClaim("realm_access");

            if (realmAccess == null) {
                return authorities;
            }

            Object rolesObject =
                    realmAccess.get("roles");

            if (rolesObject instanceof Collection<?> roles) {
                for (Object role : roles) {
                    String roleName = role.toString();

                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }

                    authorities.add(
                            new SimpleGrantedAuthority(roleName)
                    );
                }
            }

            return authorities;
        }
    }
}
