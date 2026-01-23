package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.MessMenu;
import com.hostel.smart_hostel.repository.MessMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mess")
@CrossOrigin(origins = "*")
public class MessController {

    @Autowired
    private MessMenuRepository messMenuRepository;

    @GetMapping("/all")
    public List<MessMenu> getMenu() {
        return messMenuRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody MessMenu item) {
        return ResponseEntity.ok(messMenuRepository.save(item));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id) {
        messMenuRepository.deleteById(id);
        return ResponseEntity.ok("Item removed successfully");
    }
    // --- UPDATE EXISTING ITEM ---
    @PutMapping("/update")
    public ResponseEntity<?> updateItem(@RequestBody MessMenu item) {
        return messMenuRepository.findById(item.getId()).map(existing -> {
            existing.setDay(item.getDay());
            existing.setMealType(item.getMealType());
            existing.setItemName(item.getItemName());
            return ResponseEntity.ok(messMenuRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }
}