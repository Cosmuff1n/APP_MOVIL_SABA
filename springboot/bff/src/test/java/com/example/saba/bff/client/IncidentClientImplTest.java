package com.example.saba.bff.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class IncidentClientImplTest {
    private static final String BASE = "http://localhost:8082";
    private static final String TOKEN = "Bearer prueba";

    @Test void listsIncidentsWithFiltersAndToken() {
        var builder = RestClient.builder().baseUrl(BASE);
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new IncidentClientImpl(builder.build());
        server.expect(requestTo(BASE + "/api/incidents?parkingId=7&serviceId=3"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", TOKEN))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        assertEquals("[]", client.list(7L, 3L, TOKEN).getBody());
        server.verify();
    }

    @Test void creationAndUpdateReachIncidentsViaHttp() {
        var builder = RestClient.builder().baseUrl(BASE);
        var server = MockRestServiceServer.bindTo(builder).build();
        var client = new IncidentClientImpl(builder.build());
        server.expect(requestTo(BASE + "/api/incidents"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", TOKEN))
                .andRespond(withSuccess("{\"id\":8}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/api/incidents/8"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header("Authorization", TOKEN))
                .andRespond(withSuccess("{\"id\":8}", MediaType.APPLICATION_JSON));
        assertNotNull(client.create("{}", TOKEN).getBody());
        assertNotNull(client.update(8, "{}", TOKEN).getBody());
        server.verify();
    }
}
