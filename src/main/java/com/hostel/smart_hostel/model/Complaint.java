package com.hostel.smart_hostel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String registrationNumber;
    private String complaintType; // Food, Room Appliances, Cleanliness, Water, Electricity, Other

    @Column(columnDefinition = "TEXT")
    private String description;

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(String resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageBase64; // Stores the picture

    private String status = "PENDING"; // PENDING, IN-PROGRESS, RESOLVED
    private String resolutionTime = "Awaiting Review"; // Under 2 Days, Under 1 Week, etc.
}