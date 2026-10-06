
        package com.ecommerce.gateway.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http

                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(cors -> {})

                .authorizeExchange(exchange -> exchange

                        // ==========================================
                        // CORS PREFLIGHT
                        // ==========================================
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // ==========================================
                        // PUBLIC PRODUCT IMAGES
                        // ==========================================
                        .pathMatchers(
                                HttpMethod.GET,
                                "/uploads/products/**"
                        )
                        .permitAll()

                        .pathMatchers(
                                HttpMethod.HEAD,
                                "/uploads/products/**"
                        )
                        .permitAll()

                        // ==========================================
                        // PUBLIC PRODUCT APIs
                        // ==========================================
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/products/**"
                        )
                        .permitAll()

                        // ==========================================
                        // PUBLIC CATEGORY APIs
                        // ==========================================
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        )
                        .permitAll()

                        // ==========================================
                        // PUBLIC INVENTORY APIs
                        // ==========================================
                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/inventory/**"
                        )
                        .permitAll()

                        // ==========================================
                        // PUBLIC AI MESSAGE CLASSIFICATION
                        // ==========================================
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/messages/**"
                        )
                        .permitAll()

                        // ==========================================
                        // SWAGGER / ACTUATOR
                        // ==========================================
                        .pathMatchers(
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // ==========================================
                        // EVERYTHING ELSE REQUIRES KEYCLOAK
                        // ==========================================
                        .anyExchange()
                        .authenticated()
                )

                // ==========================================
                // KEYCLOAK JWT
                // ==========================================
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )

                .build();
    }
}
