package com.hostel.smart_hostel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link this to the User account
    @Column(unique = true, nullable = false)
    private String registrationNumber; // STxxxx
    @Column(unique = true, nullable = false)
    private String fullName;
    @Column(unique = true, nullable = false)
    private String aadharNumber;
    @Column(unique = true, nullable = false)
    private String studentPhone;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String fatherName;
    @Column(unique = true, nullable = false)
    private String motherName;
    @Column(unique = true, nullable = false)
    private String parentPhone;
    @Column(nullable = false)
    private String collegeName;
    @Column(nullable = false)
    private String branch;
    @Column(nullable = false)
    private String city;
    @Column(unique = true, nullable = false)
    private String relativeContact; // Emergency contact

    public String getAssignedRoom() {
        return assignedRoom;
    }

    public void setAssignedRoom(String assignedRoom) {
        this.assignedRoom = assignedRoom;
    }

    public String getAssignedBed() {
        return assignedBed;
    }

    public void setAssignedBed(String assignedBed) {
        this.assignedBed = assignedBed;
    }

    @Column(length = 500)
    private String permanentAddress;
    @Column(name = "assigned_room")
    private String assignedRoom;

    @Column(name = "assigned_bed")
    private String assignedBed;



    // Constructors
    public Student() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String reg) { this.registrationNumber = reg; }
    public String getFullName() { return fullName; }
    public void setFullName(String name) { this.fullName = name; }
    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadhar) { this.aadharNumber = aadhar; }
    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String phone) { this.studentPhone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fn) { this.fatherName = fn; }
    public String getMotherName() { return motherName; }
    public void setMotherName(String mn) { this.motherName = mn; }
    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String pp) { this.parentPhone = pp; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String cn) { this.collegeName = cn; }
    public String getBranch() { return branch; }
    public void setBranch(String b) { this.branch = b; }
    public String getCity() { return city; }
    public void setCity(String c) { this.city = c; }
    public String getRelativeContact() { return relativeContact; }
    public void setRelativeContact(String rc) { this.relativeContact = rc; }
    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String pa) { this.permanentAddress = pa; }

}