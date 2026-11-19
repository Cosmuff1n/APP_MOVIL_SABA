package com.workshop.bff.controller;

import com.workshop.bff.client.AuthClient;
import com.workshop.bff.dto.ApiResponse;
import com.workshop.bff.dto.LoginRequest;
import com.workshop.bff.dto.RegisterRequest;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthBffControllerTest {

    @Mock
    private AuthClient authClient;

    @Test
    void register_shouldDelegateToAuthClient() {
        AuthBffController controller = new AuthBffController(authClient);
        RegisterRequest request = new RegisterRequest("charlie", "password123");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("token", "jwt-token"));

        when(authClient.register(request)).thenReturn(response);

        ResponseEntity<Object> result = controller.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(authClient).register(request);
    }

    @Test
    void login_shouldDelegateToAuthClient() {
        AuthBffController controller = new AuthBffController(authClient);
        LoginRequest request = new LoginRequest("charlie", "password123");
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("token", "jwt-token"));

        when(authClient.login(request)).thenReturn(response);

        ResponseEntity<Object> result = controller.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(authClient).login(request);
    }

    @Test
    void validate_shouldReturnBadRequestWhenHeaderIsMissing() {
        AuthBffController controller = new AuthBffController(authClient);

        ResponseEntity<Object> result = controller.validate(null);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void validate_shouldDelegateToAuthClientWhenHeaderExists() {
        AuthBffController controller = new AuthBffController(authClient);
        ResponseEntity<Object> response = ResponseEntity.ok(new ApiResponse<>(true, true));

        when(authClient.validate("Bearer valid-token")).thenReturn(response);

        ResponseEntity<Object> result = controller.validate("Bearer valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(authClient).validate("Bearer valid-token");
    }
}
