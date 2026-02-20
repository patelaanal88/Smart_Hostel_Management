package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByRegistrationNumber(String regNo);

    List<LeaveApplication> findByStatus(String status);

    // Added to check for pending spam
    List<LeaveApplication> findByRegistrationNumberAndStatus(String regNo, String status);

    // Added to check for active approved leaves
    @Query("SELECT l FROM LeaveApplication l WHERE l.registrationNumber = :regNo AND l.status = 'APPROVED' AND l.toDate >= :currentDate")
    List<LeaveApplication> findActiveApprovedLeaves(@Param("regNo") String regNo, @Param("currentDate") String currentDate);
}