package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByRegistrationNumber(String registrationNumber);
    List<Complaint> findByStatus(String status);
}