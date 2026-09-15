package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & User Management", description = "User login, session validation, and account administration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and obtain session token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate session token")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "User successfully logged out."));
    }

    @GetMapping("/users")
    @Operation(summary = "List all staff user accounts (Admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Activate or deactivate staff user account")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(authService.updateUserStatus(id, active));
    }
}
