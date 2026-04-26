package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.RoomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
    // New method to find requests by registration number and status
    List<RoomRequest> findByRegistrationNumberAndStatus(String registrationNumber, String status);
}
