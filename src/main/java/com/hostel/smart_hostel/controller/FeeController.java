package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.FeeStructure;
import com.hostel.smart_hostel.model.PaymentRecord;
import com.hostel.smart_hostel.model.Student;
import com.hostel.smart_hostel.model.Room;
import com.hostel.smart_hostel.repository.FeeRepository;
import com.hostel.smart_hostel.repository.PaymentRepository;
import com.hostel.smart_hostel.repository.StudentRepository;
import com.hostel.smart_hostel.repository.RoomRepository; // ADDED THIS
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fees")
@CrossOrigin(origins = "*")
public class FeeController {

    @Autowired private FeeRepository feeRepo;
    @Autowired private PaymentRepository payRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private RoomRepository roomRepo; // ADDED THIS TO RECOGNIZE ROOMS

    @PostMapping("/admin/update")
    public ResponseEntity<?> updateFee(@RequestBody FeeStructure fee) {
        return ResponseEntity.ok(feeRepo.save(fee));
    }

    @GetMapping("/structure/{regNo}")
    public ResponseEntity<?> getStudentFee(@PathVariable String regNo) {
        // 1. Find Student
        Optional<Student> studentOpt = studentRepo.findByRegistrationNumber(regNo);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Student not found");
        }

        Student student = studentOpt.get();
        String assignedRoomNum = student.getAssignedRoom();

        // 2. Default to NON-AC
        String feeType = "NON-AC";

        // 3. Find the Room Type from the Room table using roomNumber
        if (assignedRoomNum != null && !assignedRoomNum.isEmpty()) {
            Optional<Room> roomOpt = roomRepo.findByRoomNumber(assignedRoomNum);
            if (roomOpt.isPresent()) {
                // This pulls "AC" or "NON-AC" directly from your Room.java 'type' field
                feeType = roomOpt.get().getType().toUpperCase();
            }
        }

        // 4. Fetch the specific fee structure
        return feeRepo.findById(feeType)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Fee structure for " + feeType + " not set by Admin."));
    }

    @PostMapping("/submit-payment")
    public ResponseEntity<?> submitPayment(@RequestBody PaymentRecord pay) {
        pay.setStatus("PENDING");
        return ResponseEntity.ok(payRepo.save(pay));
    }

    @GetMapping("/admin/all-payments")
    public List<PaymentRecord> getAllPayments() {
        return payRepo.findAll().stream().map(payment -> {
            studentRepo.findByRegistrationNumber(payment.getRegistrationNumber())
                    .ifPresent(s -> payment.setStudentName(s.getFullName()));
            return payment;
        }).collect(Collectors.toList());
    }

    @GetMapping("/admin/pending")
    public List<PaymentRecord> getPending() {
        return payRepo.findByStatus("PENDING").stream().map(payment -> {
            studentRepo.findByRegistrationNumber(payment.getRegistrationNumber())
                    .ifPresent(s -> payment.setStudentName(s.getFullName()));
            return payment;
        }).collect(Collectors.toList());
    }

    @PutMapping("/admin/verify/{id}")
    public ResponseEntity<?> verifyPayment(@PathVariable Long id, @RequestParam String status) {
        return payRepo.findById(id).map(p -> {
            p.setStatus(status);
            payRepo.save(p);
            return ResponseEntity.ok("Verified");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student-status/{regNo}")
    public ResponseEntity<?> getStudentPaymentStatus(@PathVariable String regNo) {
        List<PaymentRecord> payments = payRepo.findByRegistrationNumber(regNo);
        if (payments.isEmpty()) return ResponseEntity.ok(null);
        return ResponseEntity.ok(payments.get(payments.size() - 1));
    }
}