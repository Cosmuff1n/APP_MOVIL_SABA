package com.example.saba.incidents.client;

import com.example.saba.incidents.exception.DependencyUnavailableException;
import com.example.saba.incidents.exception.InvalidIncidentException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ParkingClientImplTest {
    @Test void forwardsTokenToParkingService() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new ParkingClientImpl(builder.baseUrl("http://localhost:8081").build());
        server.expect(requestTo("http://localhost:8081/api/parkings/15"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer valid-token"))
                .andRespond(withSuccess());
        client.assertExists(15L, "Bearer valid-token");
        server.verify();
    }

    @Test void notFoundParkingIsRejected() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new ParkingClientImpl(builder.baseUrl("http://localhost:8081").build());
        server.expect(requestTo("http://localhost:8081/api/parkings/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThrows(InvalidIncidentException.class,
                () -> client.assertExists(99L, "Bearer valid-token"));
        server.verify();
    }

    @Test void serverErrorDoesNotCountAsValidParking() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new ParkingClientImpl(builder.baseUrl("http://localhost:8081").build());
        server.expect(requestTo("http://localhost:8081/api/parkings/15"))
                .andRespond(withServerError());
        assertThrows(DependencyUnavailableException.class,
                () -> client.assertExists(15L, "Bearer valid-token"));
    }
}
