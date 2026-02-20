package com.hostel.smart_hostel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FeeStructure {
    @Id
    private String roomType; // "AC" or "NON-AC"
    private double rent;
    private double mess;
    private double electricity;
    private double maintenance;
    private String adminUpiId; // Your UPI ID stored here

    public double getTotal() {
        return rent + mess + electricity + maintenance;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public double getMess() {
        return mess;
    }

    public void setMess(double mess) {
        this.mess = mess;
    }

    public double getElectricity() {
        return electricity;
    }

    public void setElectricity(double electricity) {
        this.electricity = electricity;
    }

    public double getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(double maintenance) {
        this.maintenance = maintenance;
    }

    public String getAdminUpiId() {
        return adminUpiId;
    }

    public void setAdminUpiId(String adminUpiId) {
        this.adminUpiId = adminUpiId;
    }
}