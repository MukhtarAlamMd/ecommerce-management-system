package com.ecommerce.notification.notification_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        System.out.println("========================================");
        System.out.println("KEYCLOAK REALM ACCESS: " + realmAccess);

        if (realmAccess == null) {
            System.out.println("No realm_access claim found");
            System.out.println("========================================");
            return Collections.emptyList();
        }

        Object roles = realmAccess.get("roles");

        System.out.println("KEYCLOAK ROLES: " + roles);

        if (!(roles instanceof Collection<?> roleCollection)) {
            System.out.println("No roles collection found");
            System.out.println("========================================");
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = roleCollection.stream()
                .map(Object::toString)
                .map(role -> {
                    String authority = role.startsWith("ROLE_")
                            ? role
                            : "ROLE_" + role;

                    System.out.println(
                            "Converting role: " + role +
                                    " -> " + authority
                    );

                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        System.out.println("FINAL AUTHORITIES: " + authorities);
        System.out.println("========================================");

        return authorities;
    }
}