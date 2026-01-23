package com.hostel.smart_hostel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "room_requests")
public class RoomRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationNumber;
    private String fullName;
    private String branch;
    private String currentRoom;

    // The two specific preferences
    private String preference1;
    private String preference2;

    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }
    public String getPreference1() { return preference1; }
    public void setPreference1(String preference1) { this.preference1 = preference1; }
    public String getPreference2() { return preference2; }
    public void setPreference2(String preference2) { this.preference2 = preference2; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}