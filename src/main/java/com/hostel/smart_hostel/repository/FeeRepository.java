package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<FeeStructure, String> {
}