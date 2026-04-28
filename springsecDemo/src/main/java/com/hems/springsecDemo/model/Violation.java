package com.hems.springsecDemo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "violations") // 🔥 Table name
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vehicleType;
    private int riderCount;
    private boolean helmet;
    private String numberPlate;
    private String imageUrl;
    private LocalDateTime timestamp;

    // 🔹 Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getRiderCount() {
        return riderCount;
    }

    public void setRiderCount(int riderCount) {
        this.riderCount = riderCount;
    }

    public boolean isHelmet() {
        return helmet;
    }

    public void setHelmet(boolean helmet) {
        this.helmet = helmet;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}