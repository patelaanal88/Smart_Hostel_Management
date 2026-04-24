package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.model.User;
import com.hostel.smart_hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Temporary storage for OTPs (Email -> OTP)
    private Map<String, String> otpCache = new ConcurrentHashMap<>();

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken!");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginReq) {
        String providedId = loginReq.getUsername();
        Optional<User> user = userRepository.findByRegistrationNumberOrIdentificationNumber(providedId, providedId);
        if (user.isPresent() && user.get().getPassword().equals(loginReq.getPassword())) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID or Password");
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot send verification code. Email ID is not registered!");
        }

        // Generate 5 digit code
        String otp = String.valueOf(10000 + new Random().nextInt(90000));
        otpCache.put(email, otp);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your Hostel Verification Code");
            message.setText("Your 5-digit verification code is: " + otp);
            mailSender.send(message);
            return ResponseEntity.ok("OTP Sent Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (otpCache.containsKey(email) && otpCache.get(email).equals(code)) {
            otpCache.remove(email);
            Optional<User> user = userRepository.findByEmail(email);
            return ResponseEntity.ok(user.get()); // Log user in
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Verification Code");
    }
}
