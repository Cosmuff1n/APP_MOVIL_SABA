package com.example.saba.incidents.repository;

import com.example.saba.incidents.model.IncidentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IncidentRepositoryIntegrationTest {
    @Autowired private IncidentRepository repository;

    @Test void persistsAndFiltersByParking() {
        var incident = new IncidentEntity("SABA-INTEGRATION1", 15L, 3L, "Operador",
                "Barrera", "Sin respuesta", "degradado", System.currentTimeMillis());
        var saved = repository.saveAndFlush(incident);
        assertNotNull(saved.getId());
        assertNotNull(saved.getRevision());
        assertTrue(repository.existsByTicketCode("SABA-INTEGRATION1"));
        assertEquals(1, repository.findByParkingIdOrderByReportedAtDescIdDesc(15L).size());
    }
}
