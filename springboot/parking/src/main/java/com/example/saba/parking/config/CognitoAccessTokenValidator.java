package com.example.saba.parking.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** Evita aceptar un ID token en lugar del access token de Cognito. */
public final class CognitoAccessTokenValidator implements OAuth2TokenValidator<Jwt> {
    private final String expectedClientId;

    public CognitoAccessTokenValidator(String expectedClientId) {
        this.expectedClientId = expectedClientId == null ? "" : expectedClientId.trim();
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (!"access".equals(token.getClaimAsString("token_use"))) {
            return reject("Se requiere un access token de Cognito");
        }
        if (!expectedClientId.isEmpty()
                && !expectedClientId.equals(token.getClaimAsString("client_id"))) {
            return reject("El token no corresponde al cliente Cognito configurado");
        }
        return OAuth2TokenValidatorResult.success();
    }

    private static OAuth2TokenValidatorResult reject(String message) {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", message, null));
    }
}
