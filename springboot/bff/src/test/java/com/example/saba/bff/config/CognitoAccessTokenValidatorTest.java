package com.example.saba.bff.config;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.junit.jupiter.api.Assertions.*;

class CognitoAccessTokenValidatorTest {
    private static Jwt jwt(String tokenUse, String clientId) {
        var now = Instant.now();
        return new Jwt("token-de-prueba", now, now.plusSeconds(60), Map.of("alg", "RS256"),
                Map.of("token_use", tokenUse, "client_id", clientId));
    }

    @Test void onlyAcceptsAccessTokenForExpectedClient() {
        var validator = new CognitoAccessTokenValidator("saba-client");
        assertFalse(validator.validate(jwt("access", "saba-client")).hasErrors());
        assertTrue(validator.validate(jwt("id", "saba-client")).hasErrors());
        assertTrue(validator.validate(jwt("access", "otro-client")).hasErrors());
    }
}
