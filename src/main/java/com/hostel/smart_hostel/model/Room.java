package com.hostel.smart_hostel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String roomNumber;

    @Column
    private int floor;

    @Column
    private int capacity;

    @Column
    private String type;

    // Initialize with an empty string to avoid null issues
    @Column(columnDefinition = "TEXT")
    private String assignedStudents = "";

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAssignedStudents() { return assignedStudents; }
    public void setAssignedStudents(String assignedStudents) { this.assignedStudents = assignedStudents; }
}