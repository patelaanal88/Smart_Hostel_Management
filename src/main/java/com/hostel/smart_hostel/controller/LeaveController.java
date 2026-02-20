package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.*;
import com.hostel.smart_hostel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveController {

    @Autowired private LeaveRepository leaveRepository;
    @Autowired private StudentRepository studentRepository;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveApplication leave) {
        // Fetch student from DB to get "My Detail" info
        Optional<Student> studentOpt = studentRepository.findByRegistrationNumber(leave.getRegistrationNumber());

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String regNo = leave.getRegistrationNumber();
            String today = LocalDate.now().toString();

            // Overlap/Spam checks
            List<LeaveApplication> activeLeaves = leaveRepository.findActiveApprovedLeaves(regNo, today);
            if (!activeLeaves.isEmpty()) {
                return ResponseEntity.badRequest().body("Blocked: Active Approved Leave exists.");
            }
            List<LeaveApplication> pendingLeaves = leaveRepository.findByRegistrationNumberAndStatus(regNo, "PENDING");
            if (!pendingLeaves.isEmpty()) {
                return ResponseEntity.badRequest().body("Blocked: Pending application already exists.");
            }

            // Sync from "Detail" Section
            leave.setStudentName(student.getFullName());
            leave.setRoomNumber(student.getAssignedRoom());
            leave.setParentNumber(student.getParentPhone());
            leave.setFatherName(student.getFatherName()); // <-- FETCHED FROM STUDENT DETAILS
            leave.setStatus("PENDING");

            return ResponseEntity.ok(leaveRepository.save(leave));
        }
        return ResponseEntity.badRequest().body("Error: Student registration number not found.");
    }

    @GetMapping("/admin/all")
    public List<LeaveApplication> getAllPending() {
        return leaveRepository.findByStatus("PENDING");
    }

    @GetMapping("/admin/archived")
    public List<LeaveApplication> getArchived() {
        // Returns both APPROVED and REJECTED leaves
        return leaveRepository.findAll().stream()
                .filter(l -> !l.getStatus().equals("PENDING"))
                .collect(java.util.stream.Collectors.toList());
    }

    @PutMapping("/process")
    public ResponseEntity<?> processLeave(@RequestBody LeaveApplication details) {
        return leaveRepository.findById(details.getId()).map(l -> {
            l.setStatus(details.getStatus());
            l.setRejectReason(details.getRejectReason());
            return ResponseEntity.ok(leaveRepository.save(l));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{regNo}")
    public List<LeaveApplication> getStudentLeaves(@PathVariable String regNo) {
        return leaveRepository.findByRegistrationNumber(regNo);
    }
}