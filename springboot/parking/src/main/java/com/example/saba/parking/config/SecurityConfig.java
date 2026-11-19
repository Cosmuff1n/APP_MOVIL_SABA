package com.example.saba.parking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${saba.security.cognito.issuer-uri:}") String issuerUri,
            @Value("${saba.security.cognito.client-id:}") String clientId) {
        // Sin configuración NO existe modo anónimo o de prueba inseguro.
        // Arranca el servidor para diagnóstico, pero todas las solicitudes protegidas
        // seguirán rechazándose hasta configurar un User Pool real.
        if (issuerUri == null || issuerUri.isBlank()) {
            return token -> {
                throw new BadJwtException(
                        "Configure COGNITO_ISSUER_URI antes de consumir endpoints protegidos");
            };
        }
        String issuer = issuerUri.replaceAll("/+$", "");
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(issuer + "/.well-known/jwks.json").build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                new CognitoAccessTokenValidator(clientId)));
        return decoder;
    }
}
