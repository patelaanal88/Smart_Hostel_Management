package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.User;
import com.hostel.smart_hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    /**
     * SIGNUP: Saves new user data.
     * Handles both Students and Admins based on the role and ID provided.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        // Validation: Check if username already exists to prevent duplicates
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken!");
        }
        // Hibernate saves the user data to MySQL
        return ResponseEntity.ok(userRepository.save(user));
    }

    /**
     * LOGIN: Authenticates via ID Number (STxxxx or ADxxxx) and Password.
     * It checks both ID columns in the database.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginReq) {
        // In the frontend, we pass the ST/AD ID into the "username" field of the loginRequest
        String providedId = loginReq.getUsername();

        // Use the custom repository method to search both Registration and Identification columns
        Optional<User> user = userRepository.findByRegistrationNumberOrIdentificationNumber(providedId, providedId);

        if (user.isPresent() && user.get().getPassword().equals(loginReq.getPassword())) {
            // Authentication successful: Return the full user object (including role)
            return ResponseEntity.ok(user.get());
        }

        // Authentication failed
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID or Password");
    }
}