package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.Event;
import com.hostel.smart_hostel.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired private EventRepository eventRepo;

    @GetMapping("/active")
    public List<Event> getActiveEvents() {
        return eventRepo.findByArchivedFalse();
    }

    @GetMapping("/archived")
    public List<Event> getArchivedEvents() {
        return eventRepo.findByArchivedTrue();
    }

    @PostMapping("/admin/add")
    public Event addEvent(@RequestBody Event event) {
        return eventRepo.save(event);
    }

    @PutMapping("/register/{id}")
    public void registerForEvent(@PathVariable Long id) {
        eventRepo.findById(id).ifPresent(e -> {
            e.setRegistrationCount(e.getRegistrationCount() + 1);
            eventRepo.save(e);
        });
    }

    @PutMapping("/admin/update-images/{id}")
    public ResponseEntity<?> updateEventImages(@PathVariable Long id, @RequestBody List<String> images) {
        return eventRepo.findById(id).map(event -> {
            // This adds new photos to whatever is already there
            event.getImages().addAll(images);
            eventRepo.save(event);
            return ResponseEntity.ok("Gallery Updated");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/archive/{id}")
    public void archiveEvent(@PathVariable Long id) {
        eventRepo.findById(id).ifPresent(e -> {
            e.setArchived(true);
            eventRepo.save(e);
        });
    }
}