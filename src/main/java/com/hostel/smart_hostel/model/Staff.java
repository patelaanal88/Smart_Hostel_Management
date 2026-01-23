package com.hostel.smart_hostel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hostel_staff")
@Data
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDutyType() {
        return dutyType;
    }

    public void setDutyType(String dutyType) {
        this.dutyType = dutyType;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    private String name;
    private String role;        // Rector, Warden, Watchman
    private String dutyType;    // Day, Night
    private String work;        // Description of duties
    private String mobile;      // 10-digit validation handled in frontend
}