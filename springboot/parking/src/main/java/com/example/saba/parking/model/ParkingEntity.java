package com.example.saba.parking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "parkings")
public class ParkingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address = "";

    @Column(name = "commune", nullable = false, length = 120)
    private String commune = "";

    @Column(name = "total_spaces", nullable = false)
    private int totalSpaces;

    @Column(name = "free_spaces", nullable = false)
    private int freeSpaces;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // JPA incrementa la revision automáticamente en cada actualización.
    @Version
    @Column(name = "revision", nullable = false)
    private Long revision;

    // Milisegundos desde Unix Epoch, igual que Ec2ParkingDto.kt.
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    protected ParkingEntity() { }

    public ParkingEntity(String name, String address, String commune,
                         int totalSpaces, int freeSpaces, boolean isActive,
                         long updatedAt) {
        this.name = name;
        this.address = address;
        this.commune = commune;
        this.totalSpaces = totalSpaces;
        this.freeSpaces = freeSpaces;
        this.isActive = isActive;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCommune() { return commune; }
    public int getTotalSpaces() { return totalSpaces; }
    public int getFreeSpaces() { return freeSpaces; }
    public boolean isActive() { return isActive; }
    public Long getRevision() { return revision; }
    public Long getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
