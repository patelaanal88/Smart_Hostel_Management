package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.*;
import com.hostel.smart_hostel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JavaMailSender mailSender;

    @PostMapping("/submit")
    public ResponseEntity<?> submitComplaint(@RequestBody Complaint complaint) {
        Optional<Student> studentOpt = studentRepository.findByRegistrationNumber(complaint.getRegistrationNumber());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            complaint.setStudentName(student.getFullName());
            complaint.setRoomNumber(student.getAssignedRoom());
            complaint.setStatus("PENDING");
            complaint.setResolutionTime("Awaiting Review");
            return ResponseEntity.ok(complaintRepository.save(complaint));
        }
        return ResponseEntity.badRequest().body("Error: Student Registration Number not found.");
    }

    @GetMapping("/student/{regNo}")
    public List<Complaint> getStudentComplaints(@PathVariable String regNo) {
        return complaintRepository.findByRegistrationNumber(regNo);
    }

    @GetMapping("/admin/all")
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @GetMapping("/admin/archived")
    public List<Complaint> getArchived() {
        return complaintRepository.findByStatus("RESOLVED");
    }

    @PutMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestBody Complaint details) {
        return complaintRepository.findById(details.getId()).map(c -> {
            if (details.getStatus() != null) c.setStatus(details.getStatus());
            if (details.getResolutionTime() != null) c.setResolutionTime(details.getResolutionTime());
            Complaint updatedComplaint = complaintRepository.save(c);

            // Notify if Resolved
            if ("RESOLVED".equalsIgnoreCase(updatedComplaint.getStatus())) {
                sendComplaintResolvedEmail(updatedComplaint);
            }

            return ResponseEntity.ok(updatedComplaint);
        }).orElse(ResponseEntity.notFound().build());
    }

    private void sendComplaintResolvedEmail(Complaint complaint) {
        userRepository.findByRegistrationNumberOrIdentificationNumber(complaint.getRegistrationNumber(), complaint.getRegistrationNumber())
                .ifPresent(user -> {
                    try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(user.getEmail());
                        message.setSubject("Complaint Resolved - Smart Hostel");
                        message.setText("Dear " + complaint.getStudentName() + ",\n\n" +
                                "Your complaint regarding '" + complaint.getType() + "' has been marked as RESOLVED.\n" +
                                "Resolution Note: " + complaint.getResolutionTime() + "\n\n" +
                                "If you are not satisfied, please visit the hostel office.\n\n" +
                                "Regards,\nHostel Administration");
                        mailSender.send(message);
                    } catch (Exception e) {
                        System.err.println("Mail Error: " + e.getMessage());
                    }
                });
    }
}
