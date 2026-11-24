package com.example.saba.bff.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Solo se admiten access tokens del cliente Cognito público configurado.
 */
public final class CognitoAccessTokenValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedClientId;

    public CognitoAccessTokenValidator(String expectedClientId) {
        this.expectedClientId = expectedClientId == null ? "" : expectedClientId.trim();
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (!"access".equals(jwt.getClaimAsString("token_use"))) {
            return reject("Se requiere un access token de Cognito");
        }
        if (!expectedClientId.isBlank()
                && !expectedClientId.equals(jwt.getClaimAsString("client_id"))) {
            return reject("Client ID de Cognito no autorizado");
        }
        return OAuth2TokenValidatorResult.success();
    }

    private static OAuth2TokenValidatorResult reject(String message) {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", message, null));
    }
}
