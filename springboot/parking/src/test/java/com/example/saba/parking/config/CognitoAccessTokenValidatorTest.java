package com.example.saba.parking.config;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.junit.jupiter.api.Assertions.*;

class CognitoAccessTokenValidatorTest {
    private Jwt jwt(String type, String clientId) {
        Instant now = Instant.now();
        return Jwt.withTokenValue("fake-for-unit-test-only")
                .header("alg", "RS256")
                .claim("token_use", type)
                .claim("client_id", clientId)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(600))
                .build();
    }

    @Test void acceptsCorrectAccessTokenClaims() {
        assertFalse(new CognitoAccessTokenValidator("saba-client")
                .validate(jwt("access", "saba-client")).hasErrors());
    }

    @Test void rejectsIdToken() {
        assertTrue(new CognitoAccessTokenValidator("saba-client")
                .validate(jwt("id", "saba-client")).hasErrors());
    }

    @Test void rejectsWrongClientId() {
        assertTrue(new CognitoAccessTokenValidator("saba-client")
                .validate(jwt("access", "another-client")).hasErrors());
    }
}
