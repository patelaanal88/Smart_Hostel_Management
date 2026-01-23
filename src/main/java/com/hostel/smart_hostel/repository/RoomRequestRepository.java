package com.hostel.smart_hostel.repository;



import com.hostel.smart_hostel.model.RoomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
}