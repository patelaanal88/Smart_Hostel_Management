package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);

    // Only fetch students where assignedRoom is null or empty
    @Query("SELECT s FROM Student s WHERE s.assignedRoom IS NULL OR s.assignedRoom = ''")
    List<Student> findUnassignedStudents();
}