package com.workshop.bff.filter;

import com.workshop.bff.client.AuthClient;
import jakarta.servlet.FilterChain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class BffAuthenticationFilterTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private FilterChain filterChain;

    @Test
    void doFilter_shouldRejectProtectedRequestWhenAuthorizationHeaderIsMissing() throws Exception {
        BffAuthenticationFilter filter = new BffAuthenticationFilter(authClient, new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/customers/all");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_shouldRejectProtectedRequestWhenTokenIsInvalid() throws Exception {
        BffAuthenticationFilter filter = new BffAuthenticationFilter(authClient, new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/customers/all");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer fake-token");
        when(authClient.isTokenValid("Bearer fake-token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_shouldContinueProtectedRequestWhenTokenIsValid() throws Exception {
        BffAuthenticationFilter filter = new BffAuthenticationFilter(authClient, new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/customers/all");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");
        when(authClient.isTokenValid("Bearer valid-token")).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doFilter_shouldSkipPublicLoginRoute() throws Exception {
        BffAuthenticationFilter filter = new BffAuthenticationFilter(authClient, new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(authClient, never()).isTokenValid(any());
        verify(filterChain).doFilter(any(), any());
    }
}
