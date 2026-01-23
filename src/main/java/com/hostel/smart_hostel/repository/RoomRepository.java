package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Finds a room by its unique room number (e.g., "101").
     * Used during the bed assignment process to link a student to a specific room.
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Finds all rooms located on a specific floor.
     * Used by the Admin Grid to filter the view (e.g., only show Floor 1).
     */
    List<Room> findByFloor(int floor);

    /**
     * Optional: Checks if a room number already exists before adding a new one.
     * This prevents duplicate room entries in the database.
     */
    boolean existsByRoomNumber(String roomNumber);
}