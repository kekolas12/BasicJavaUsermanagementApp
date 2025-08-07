package mertcan.usermanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long userCount = userRepository.count();
        boolean adminExists = userRepository.existsByUsername("admin");
        
        stats.put("totalUsers", userCount);
        stats.put("adminExists", adminExists);
        
        log.info("Debug stats - Users: {}, Admin exists: {}", userCount, adminExists);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            result.put("error", "Admin user not found");
            return ResponseEntity.ok(result);
        }
        
        String storedPassword = admin.getPassword();
        boolean matches = passwordEncoder.matches(password, storedPassword);
        
        result.put("providedPassword", password);
        result.put("storedPasswordHash", storedPassword);
        result.put("passwordMatches", matches);
        result.put("encodedTestPassword", passwordEncoder.encode(password));
        
        log.info("Password test - Provided: {}, Matches: {}", password, matches);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/reset-admin-password")
    public ResponseEntity<Map<String, Object>> resetAdminPassword() {
        Map<String, Object> result = new HashMap<>();
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            result.put("error", "Admin user not found");
            return ResponseEntity.ok(result);
        }
        
        String newPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(encodedPassword);
        userRepository.save(admin);
        
        result.put("message", "Admin password reset successfully");
        result.put("newPassword", newPassword);
        result.put("encodedPassword", encodedPassword);
        
        log.info("Admin password reset to: {}", newPassword);
        
        return ResponseEntity.ok(result);
    }
}
