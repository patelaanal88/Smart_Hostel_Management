package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.Staff;
import com.hostel.smart_hostel.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("/all")
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @PostMapping("/add")
    public Staff addStaff(@RequestBody Staff staff) {
        return staffRepository.save(staff);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateStaff(@RequestBody Staff staff) {
        return staffRepository.findById(staff.getId()).map(existing -> {
            existing.setName(staff.getName());
            existing.setRole(staff.getRole());
            existing.setDutyType(staff.getDutyType());
            existing.setWork(staff.getWork());
            existing.setMobile(staff.getMobile());
            return ResponseEntity.ok(staffRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        staffRepository.deleteById(id);
        return ResponseEntity.ok("Staff record deleted successfully");
    }
}