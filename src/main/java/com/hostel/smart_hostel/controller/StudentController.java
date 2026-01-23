package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.*;
import com.hostel.smart_hostel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRequestRepository roomRequestRepository;

    // --- 1. STUDENT PROFILE SAVING (STRICTLY KEPT) ---
    @PostMapping("/save")
    public ResponseEntity<?> saveDetails(@RequestBody Student student) {
        return studentRepository.findByRegistrationNumber(student.getRegistrationNumber())
                .map(existing -> {
                    student.setId(existing.getId());
                    return ResponseEntity.ok(studentRepository.save(student));
                })
                .orElseGet(() -> ResponseEntity.ok(studentRepository.save(student)));
    }

    // --- 2. GET ALL STUDENTS (STRICTLY KEPT) ---
    @GetMapping("/all")
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    // --- 3. ADMIN ROOM ASSIGNMENT LOGIC (STRICTLY KEPT) ---
    @GetMapping("/unassigned")
    public List<Student> getUnassigned() {
        return studentRepository.findUnassignedStudents();
    }
    @PutMapping("/assignRoom")
    public ResponseEntity<?> assignRoom(@RequestBody Map<String, String> payload) {
        return studentRepository.findByRegistrationNumber(payload.get("registrationNumber")).map(s -> {
            s.setAssignedRoom(payload.get("assignedRoom"));
            s.setAssignedBed(payload.get("assignedBed"));
            return ResponseEntity.ok(studentRepository.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- 4. STUDENT: REQUEST ROOM CHANGE (STRICTLY KEPT/UPDATED) ---
    @PostMapping("/requestChange")
    public ResponseEntity<?> requestChange(@RequestBody RoomRequest req) {
        req.setStatus("PENDING"); // Default status for new requests
        return ResponseEntity.ok(roomRequestRepository.save(req));
    }

    // --- 5. ADMIN: VIEW ALL REQUESTS (STRICTLY KEPT) ---
    @GetMapping("/requests/all")
    public List<RoomRequest> getAllRequests() {
        return roomRequestRepository.findAll();
    }


    // --- 6. ADMIN: PROCESS ACCEPT/REJECT (NEW LOGIC ADDED - NO REMOVALS) ---
    @PutMapping("/requests/process")
    public ResponseEntity<?> processRequest(@RequestBody Map<String, String> payload) {
        Long id = Long.parseLong(payload.get("id"));
        String action = payload.get("action"); // Expecting "ACCEPT" or "REJECT"

        return roomRequestRepository.findById(id).map(req -> {
            if ("ACCEPT".equals(action)) {
                // When accepted, clear current room assignment so admin can re-assign manually
                studentRepository.findByRegistrationNumber(req.getRegistrationNumber()).ifPresent(s -> {
                    s.setAssignedRoom(""); // Clear old room
                    s.setAssignedBed("");  // Clear old bed
                    studentRepository.save(s);
                });
                req.setStatus("ACCEPTED");
            } else if ("REJECT".equals(action)) {
                req.setStatus("REJECTED");
            }

            roomRequestRepository.save(req);
            return ResponseEntity.ok("Request Processed Successfully as " + action);
        }).orElse(ResponseEntity.notFound().build());
    }
}