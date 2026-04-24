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

    @Autowired
    private RoomRepository roomRepository;

    // --- NEW: GET STUDENT BY REGISTRATION NUMBER ---
    // This is required for your PDF to fetch Name and Room No from the DB
    @GetMapping("/{regNo}")
    public ResponseEntity<Student> getStudent(@PathVariable String regNo) {
        return studentRepository.findByRegistrationNumber(regNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 1. STUDENT PROFILE SAVING ---
    @PostMapping("/save")
    public ResponseEntity<?> saveDetails(@RequestBody Student student) {
        return studentRepository.findByRegistrationNumber(student.getRegistrationNumber())
                .map(existing -> {
                    student.setId(existing.getId());
                    return ResponseEntity.ok(studentRepository.save(student));
                })
                .orElseGet(() -> ResponseEntity.ok(studentRepository.save(student)));
    }

    // --- 2. GET ALL STUDENTS ---
    @GetMapping("/all")
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    // --- 3. ADMIN ROOM ASSIGNMENT LOGIC ---
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

    // --- 4. STUDENT: REQUEST ROOM CHANGE (UPDATED WITH PENDING CHECK) ---
    @PostMapping("/requestChange")
    public ResponseEntity<?> requestChange(@RequestBody RoomRequest req) {
        // Check if a PENDING request already exists for this student
        List<RoomRequest> pending = roomRequestRepository
                .findByRegistrationNumberAndStatus(req.getRegistrationNumber(), "PENDING");

        if (!pending.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("You already have a pending request. Please wait for Admin response.");
        }

        req.setStatus("PENDING");
        return ResponseEntity.ok(roomRequestRepository.save(req));
    }

    // --- 5. ADMIN: VIEW ALL REQUESTS ---
    @GetMapping("/requests/all")
    public List<RoomRequest> getAllRequests() {
        return roomRequestRepository.findAll();
    }

    // --- 6. ADMIN: PROCESS REQUEST (AUTO-VACATE LOGIC) ---
    @PutMapping("/requests/process")
    public ResponseEntity<?> processRequest(@RequestBody Map<String, Object> payload) {
        Long id = Long.valueOf(payload.get("id").toString());
        String action = payload.get("action").toString(); // "ACCEPT" or "REJECT"

        return roomRequestRepository.findById(id).map(req -> {
            req.setStatus(action + "ED");

            if ("ACCEPT".equals(action)) {
                // AUTO-VACATE LOGIC: Clears Room record and Student record automatically
                studentRepository.findByRegistrationNumber(req.getRegistrationNumber()).ifPresent(student -> {
                    String oldRoomNo = student.getAssignedRoom();

                    if (oldRoomNo != null && !oldRoomNo.trim().isEmpty()) {
                        roomRepository.findByRoomNumber(oldRoomNo).ifPresent(room -> {
                            String[] occupants = room.getAssignedStudents().split(",", -1);
                            for (int i = 0; i < occupants.length; i++) {
                                if (occupants[i].trim().equals(student.getRegistrationNumber().trim())) {
                                    occupants[i] = " ";
                                }
                            }
                            room.setAssignedStudents(String.join(",", occupants));
                            roomRepository.save(room);
                        });
                    }
                    // Reset student assignment so they appear as unassigned
                    student.setAssignedRoom("");
                    student.setAssignedBed("");
                    studentRepository.save(student);
                });
            }

            roomRequestRepository.save(req);
            return ResponseEntity.ok("Request " + action + "ED successfully.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
