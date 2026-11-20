package com.example.saba.bff.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ParkingClientImplTest {
    private static final String TOKEN = "Bearer token-de-prueba";
    private static final String BASE = "http://localhost:8081";

    @Test void getUsesSingularParkingRouteAndForwardsToken() {
        var builder = RestClient.builder().baseUrl(BASE);
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new ParkingClientImpl(builder.build());
        server.expect(requestTo(BASE + "/api/parking"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", TOKEN))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        assertEquals("[]", client.list(TOKEN).getBody());
        server.verify();
    }

    @Test void postPreservesJsonAndReturnsCreatedStatus() {
        var builder = RestClient.builder().baseUrl(BASE);
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new ParkingClientImpl(builder.build());
        String body = "{\"name\":\"Parking prueba\"}";
        server.expect(requestTo(BASE + "/api/parking"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(content().json(body))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("{\"id\":1}"));
        assertEquals(HttpStatus.CREATED, client.create(body, TOKEN).getStatusCode());
        server.verify();
    }

    @Test void patchAndDeletePreserveRevisionAndToken() {
        var builder = RestClient.builder().baseUrl(BASE);
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new ParkingClientImpl(builder.build());
        server.expect(requestTo(BASE + "/api/parking/17"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(content().json("{\"revision\":0,\"isActive\":false}"))
                .andRespond(withSuccess("{\"id\":17}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/api/parking/17"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(header("If-Match", "1"))
                .andRespond(withNoContent());
        client.patch(17L, "{\"revision\":0,\"isActive\":false}", TOKEN);
        assertEquals(HttpStatus.NO_CONTENT, client.delete(17L, "1", TOKEN).getStatusCode());
        server.verify();
    }
}
