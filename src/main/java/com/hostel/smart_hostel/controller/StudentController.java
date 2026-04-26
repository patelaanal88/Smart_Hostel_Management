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

    @Autowired private StudentRepository studentRepository;
    @Autowired private RoomRequestRepository roomRequestRepository;
    @Autowired private RoomRepository roomRepository;

    @GetMapping("/{regNo}")
    public ResponseEntity<Student> getStudent(@PathVariable String regNo) {
        return studentRepository.findByRegistrationNumber(regNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveDetails(@RequestBody Student student) {
        try {
            return studentRepository.findByRegistrationNumber(student.getRegistrationNumber())
                    .map(existing -> {
                        // Update existing fields but keep the ID and Room assignments
                        student.setId(existing.getId());
                        student.setAssignedRoom(existing.getAssignedRoom());
                        student.setAssignedBed(existing.getAssignedBed());
                        return ResponseEntity.ok(studentRepository.save(student));
                    })
                    .orElseGet(() -> ResponseEntity.ok(studentRepository.save(student)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Database Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

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

    @PostMapping("/requestChange")
    public ResponseEntity<?> requestChange(@RequestBody RoomRequest req) {
        List<RoomRequest> pending = roomRequestRepository
                .findByRegistrationNumberAndStatus(req.getRegistrationNumber(), "PENDING");

        if (!pending.isEmpty()) {
            return ResponseEntity.badRequest().body("You already have a pending request.");
        }

        req.setStatus("PENDING");
        return ResponseEntity.ok(roomRequestRepository.save(req));
    }

    @GetMapping("/requests/all")
    public List<RoomRequest> getAllRequests() {
        return roomRequestRepository.findAll();
    }

    @PutMapping("/requests/process")
    public ResponseEntity<?> processRequest(@RequestBody Map<String, Object> payload) {
        Long id = Long.valueOf(payload.get("id").toString());
        String action = payload.get("action").toString();

        return roomRequestRepository.findById(id).map(req -> {
            req.setStatus(action + "ED");
            if ("ACCEPT".equals(action)) {
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
                    student.setAssignedRoom("");
                    student.setAssignedBed("");
                    studentRepository.save(student);
                });
            }
            roomRequestRepository.save(req);
            return ResponseEntity.ok("Request processed.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
