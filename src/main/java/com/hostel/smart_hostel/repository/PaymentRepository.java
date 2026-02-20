package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByStatus(String status);
    List<PaymentRecord> findByRegistrationNumber(String registrationNumber);
}