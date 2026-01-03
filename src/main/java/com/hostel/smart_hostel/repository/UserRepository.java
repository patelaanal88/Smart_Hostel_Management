package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // This allows the login to check both columns with one value
    Optional<User> findByRegistrationNumberOrIdentificationNumber(String regNo, String idNo);
}
// THIS IS UserRepository.java it is interface