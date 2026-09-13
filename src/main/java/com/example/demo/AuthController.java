package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Hardcoded credentials for now - in a real app, this would check a database
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password123";

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (VALID_USERNAME.equals(request.username()) && VALID_PASSWORD.equals(request.password())) {
            String token = jwtService.generateToken(request.username());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}