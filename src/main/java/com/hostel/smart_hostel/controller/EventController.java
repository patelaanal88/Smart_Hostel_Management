package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.Event;
import com.hostel.smart_hostel.model.User;
import com.hostel.smart_hostel.repository.EventRepository;
import com.hostel.smart_hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired private EventRepository eventRepo;
    @Autowired private UserRepository userRepository;
    @Autowired private JavaMailSender mailSender;

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
        // 1. Save the event first
        Event savedEvent = eventRepo.save(event);

        // 2. Fetch all student emails from the database
        List<User> students = userRepository.findAll();
        List<String> emailList = students.stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .map(User::getEmail)
                .collect(Collectors.toList());

        // 3. Send notification emails if there are students registered
        if (!emailList.isEmpty()) {
            sendEventNotification(emailList, savedEvent);
        }

        return savedEvent;
    }

    private void sendEventNotification(List<String> emails, Event event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("New Hostel Event: " + event.getTitle());
            message.setText("Hello Students,\n\n" +
                    "A new event has been launched!\n\n" +
                    "Event: " + event.getTitle() + "\n" +
                    "Date: " + event.getEventDate() + "\n" +
                    "Category: " + event.getCategory() + "\n" +
                    "Description: " + event.getDescription() + "\n\n" +
                    "Check the student dashboard for more details.");

            // Send to all students as BCC to hide the email list from each other
            message.setBcc(emails.toArray(new String[0]));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send event notifications: " + e.getMessage());
        }
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
