package com.hostel.smart_hostel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String registrationNumber;
    private String roomNumber;
    private String parentNumber;
    private String fatherName; // Fetched from your Details section

    private String reason;
    private String fromDate;
    private String toDate;
    private String departureTime;
    private String arrivalTime;

    private String status = "PENDING";
    private String rejectReason;

    // Standard Getters and Setters (Lombok @Data handles this, but manual ones kept for safety)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getParentNumber() { return parentNumber; }
    public void setParentNumber(String parentNumber) { this.parentNumber = parentNumber; }
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }
    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}