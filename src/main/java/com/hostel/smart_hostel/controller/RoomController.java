package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.Room;
import com.hostel.smart_hostel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/all")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        if(roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            return ResponseEntity.badRequest().body("Error: Room Number already exists!");
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < room.getCapacity(); i++) {
            sb.append(" ");
            if(i < room.getCapacity() - 1) sb.append(",");
        }
        room.setAssignedStudents(sb.toString());
        return ResponseEntity.ok(roomRepository.save(room));
    }

    // UPDATED: Dynamic Update Logic
    @PutMapping("/update")
    public ResponseEntity<?> updateRoom(@RequestBody Room details) {
        return roomRepository.findByRoomNumber(details.getRoomNumber()).map(room -> {
            room.setCapacity(details.getCapacity());
            room.setType(details.getType());
            room.setFloor(details.getFloor());

            // Note: assignedStudents string logic should ideally be updated if capacity changes
            // For now, we keep existing assignments intact.
            roomRepository.save(room);
            return ResponseEntity.ok("Room Updated Successfully");
        }).orElse(ResponseEntity.status(404).body("Error: Room Number not found!"));
    }

    // UPDATED: Protected Delete Logic
    @DeleteMapping("/delete/{roomNumber}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomNumber) {
        Optional<Room> roomOpt = roomRepository.findByRoomNumber(roomNumber);

        if(roomOpt.isPresent()) {
            Room room = roomOpt.get();
            // Check if any bed is NOT empty
            String[] occupants = room.getAssignedStudents().split(",", -1);
            boolean hasStudents = Arrays.stream(occupants).anyMatch(s -> s != null && !s.trim().isEmpty());

            if(hasStudents) {
                return ResponseEntity.badRequest().body("Students are already assigned in this room! Please reallocate them before deleting.");
            }

            roomRepository.delete(room);
            return ResponseEntity.ok("Room deleted successfully");
        }
        return ResponseEntity.status(404).body("Error: Room not found!");
    }

    @PutMapping("/assignBed")
    public ResponseEntity<?> assignBed(@RequestBody Map<String, String> payload) {
        String roomNo = payload.get("roomNumber");
        String regNum = payload.get("registrationNumber");
        int bedIndex = Integer.parseInt(payload.get("bedIndex"));

        return roomRepository.findByRoomNumber(roomNo).map(room -> {
            String[] occupants = room.getAssignedStudents().split(",", -1);
            occupants[bedIndex] = regNum;
            room.setAssignedStudents(String.join(",", occupants));
            roomRepository.save(room);
            return ResponseEntity.ok("Assigned");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/vacateBed")
    public ResponseEntity<?> vacateBed(@RequestBody Map<String, String> payload) {
        String roomNo = payload.get("roomNumber");
        int bedIndex = Integer.parseInt(payload.get("bedIndex"));

        return roomRepository.findByRoomNumber(roomNo).map(room -> {
            String[] occupants = room.getAssignedStudents().split(",", -1);
            occupants[bedIndex] = " ";
            room.setAssignedStudents(String.join(",", occupants));
            roomRepository.save(room);
            return ResponseEntity.ok("Vacated");
        }).orElse(ResponseEntity.notFound().build());
    }
}