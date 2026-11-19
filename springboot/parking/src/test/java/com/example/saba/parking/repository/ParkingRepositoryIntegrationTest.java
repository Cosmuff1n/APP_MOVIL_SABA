package com.example.saba.parking.repository;

import com.example.saba.parking.model.ParkingEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParkingRepositoryIntegrationTest {
    @Autowired ParkingRepository repository;

    @Test void persistsAndReadsParking() {
        var entity = new ParkingEntity("Central", "Av 10", "Santiago", 100, 30, true,
                System.currentTimeMillis());
        var saved = repository.saveAndFlush(entity);
        assertNotNull(saved.getId());
        assertNotNull(saved.getRevision());
        assertEquals("Central", repository.findById(saved.getId()).orElseThrow().getName());
    }
}
