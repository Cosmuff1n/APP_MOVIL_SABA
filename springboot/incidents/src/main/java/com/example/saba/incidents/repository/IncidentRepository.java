package com.example.saba.incidents.repository;

import com.example.saba.incidents.model.IncidentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Consultas a la BD exclusiva de Incidencias.
 */
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {

    List<IncidentEntity> findAllByOrderByReportedAtDescIdDesc();

    List<IncidentEntity> findByParkingIdOrderByReportedAtDescIdDesc(Long parkingId);

    List<IncidentEntity> findByServiceIdOrderByReportedAtDescIdDesc(Long serviceId);

    List<IncidentEntity> findByParkingIdAndServiceIdOrderByReportedAtDescIdDesc(Long parkingId, Long serviceId);

    boolean existsByTicketCode(String ticketCode);
}
