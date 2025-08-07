package mertcan.usermanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.entity.User;
import mertcan.usermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class SimpleLoginController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/simple-login")
    public ResponseEntity<Map<String, Object>> simpleLogin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        
        log.info("Simple login attempt for: {}", username);
        
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.ok(response);
        }
        
        // Check password using passwordEncoder
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        
        response.put("success", passwordMatches);
        response.put("message", passwordMatches ? "Login successful" : "Invalid password");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("passwordMatches", passwordMatches);
        
        log.info("Simple login result for {}: {}", username, passwordMatches);
        
        return ResponseEntity.ok(response);
    }
}
