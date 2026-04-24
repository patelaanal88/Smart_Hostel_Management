package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.*;
import com.hostel.smart_hostel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    @Autowired private UserRepository userRepository;
    @Autowired private JavaMailSender mailSender;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveApplication leave) {
        Optional<Student> studentOpt = studentRepository.findByRegistrationNumber(leave.getRegistrationNumber());

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String regNo = leave.getRegistrationNumber();
            String today = LocalDate.now().toString();

            List<LeaveApplication> activeLeaves = leaveRepository.findActiveApprovedLeaves(regNo, today);
            if (!activeLeaves.isEmpty()) {
                return ResponseEntity.badRequest().body("Blocked: Active Approved Leave exists.");
            }
            List<LeaveApplication> pendingLeaves = leaveRepository.findByRegistrationNumberAndStatus(regNo, "PENDING");
            if (!pendingLeaves.isEmpty()) {
                return ResponseEntity.badRequest().body("Blocked: Pending application already exists.");
            }

            leave.setStudentName(student.getFullName());
            leave.setRoomNumber(student.getAssignedRoom());
            leave.setParentNumber(student.getParentPhone());
            leave.setFatherName(student.getFatherName());
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
        return leaveRepository.findAll().stream()
                .filter(l -> !l.getStatus().equals("PENDING"))
                .collect(java.util.stream.Collectors.toList());
    }

    @PutMapping("/process")
    public ResponseEntity<?> processLeave(@RequestBody LeaveApplication details) {
        return leaveRepository.findById(details.getId()).map(l -> {
            l.setStatus(details.getStatus());
            l.setRejectReason(details.getRejectReason());
            LeaveApplication updatedLeave = leaveRepository.save(l);

            // Send Notification
            sendLeaveStatusEmail(updatedLeave);

            return ResponseEntity.ok(updatedLeave);
        }).orElse(ResponseEntity.notFound().build());
    }

    private void sendLeaveStatusEmail(LeaveApplication leave) {
        userRepository.findByRegistrationNumberOrIdentificationNumber(leave.getRegistrationNumber(), leave.getRegistrationNumber())
                .ifPresent(user -> {
                    try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(user.getEmail());
                        message.setSubject("Leave Application Update - Smart Hostel");
                        String statusMsg = leave.getStatus().equalsIgnoreCase("APPROVED") ?
                                "Your leave has been APPROVED." :
                                "Your leave has been REJECTED. Reason: " + leave.getRejectReason();

                        message.setText("Dear " + leave.getStudentName() + ",\n\n" +
                                statusMsg + "\n" +
                                "Leave Dates: " + leave.getFromDate() + " to " + leave.getToDate() + "\n\n" +
                                "Regards,\nHostel Administration");
                        mailSender.send(message);
                    } catch (Exception e) {
                        System.err.println("Mail Error: " + e.getMessage());
                    }
                });
    }

    @GetMapping("/student/{regNo}")
    public List<LeaveApplication> getStudentLeaves(@PathVariable String regNo) {
        return leaveRepository.findByRegistrationNumber(regNo);
    }
}
