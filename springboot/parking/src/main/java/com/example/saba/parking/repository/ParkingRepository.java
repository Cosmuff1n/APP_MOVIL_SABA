package com.example.saba.parking.repository;

import com.example.saba.parking.model.ParkingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Este Repository consulta PostgreSQL; no es ParkingRepository.kt de Android.
 */
public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {

    List<ParkingEntity> findAllByOrderByNameAsc();
}
