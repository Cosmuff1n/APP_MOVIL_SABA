package com.example.saba.incidents.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/** Una fila de PostgreSQL; no es IncidentEntity.kt de Room. */
@Entity
@Table(name = "incidents")
public class IncidentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_code", nullable = false, unique = true, length = 60)
    private String ticketCode;

    @Column(name = "parking_id", nullable = false)
    private Long parkingId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "reporter_name", nullable = false, length = 150)
    private String reporterName = "";

    @Column(name = "failure_type", nullable = false, length = 120)
    private String failureType;

    @Column(name = "description", nullable = false, length = 240)
    private String description;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "reported_at", nullable = false)
    private Long reportedAt;

    @Column(name = "is_resolved", nullable = false)
    private boolean resolved;

    @Column(name = "resolved_by_name", length = 150)
    private String resolvedByName;

    @Column(name = "resolution_description", length = 300)
    private String resolutionDescription;

    @Column(name = "resolved_at")
    private Long resolvedAt;

    @Version
    @Column(name = "revision", nullable = false)
    private Long revision;

    protected IncidentEntity() { }

    public IncidentEntity(String ticketCode, Long parkingId, Long serviceId,
                          String reporterName, String failureType, String description,
                          String status, long reportedAt) {
        this.ticketCode = ticketCode;
        this.parkingId = parkingId;
        this.serviceId = serviceId;
        this.reporterName = reporterName;
        this.failureType = failureType;
        this.description = description;
        this.status = status;
        this.reportedAt = reportedAt;
    }

    public Long getId() { return id; }
    public String getTicketCode() { return ticketCode; }
    public Long getParkingId() { return parkingId; }
    public Long getServiceId() { return serviceId; }
    public String getReporterName() { return reporterName; }
    public String getFailureType() { return failureType; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public Long getReportedAt() { return reportedAt; }
    public boolean isResolved() { return resolved; }
    public String getResolvedByName() { return resolvedByName; }
    public String getResolutionDescription() { return resolutionDescription; }
    public Long getResolvedAt() { return resolvedAt; }
    public Long getRevision() { return revision; }

    public void setFailureType(String failureType) { this.failureType = failureType; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void resolve(String byName, String resolution, long when) {
        this.resolved = true;
        this.resolvedByName = byName;
        this.resolutionDescription = resolution;
        this.resolvedAt = when;
    }
}
