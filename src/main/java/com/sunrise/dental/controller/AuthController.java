package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.AdminPasswordResetRequest;
import com.sunrise.dental.dto.request.ChangePasswordRequest;
import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.request.UserCreateRequest;
import com.sunrise.dental.dto.request.UserUpdateRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.dto.response.PermissionResponse;
import com.sunrise.dental.dto.response.UserResponse;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & User Management", description = "User login, session validation, staff accounts, and granular permission administration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and obtain session token with effective permissions")
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
    @Operation(summary = "List all staff user accounts with status and assigned permissions")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUserResponses());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user account details by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserResponseById(id));
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new staff user account with custom permissions")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = authService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user account profile, role, and permissions")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = authService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a staff user account")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User account successfully deleted."));
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Activate or deactivate staff user account")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(authService.updateUserStatus(id, active));
    }

    @PostMapping("/users/{id}/unlock")
    @Operation(summary = "Unlock a locked user account and reset failed login attempts")
    public ResponseEntity<Map<String, String>> unlockUser(@PathVariable Long id) {
        authService.unlockUser(id);
        return ResponseEntity.ok(Map.of("message", "User account has been unlocked successfully."));
    }

    @PostMapping("/users/{id}/reset-password")
    @Operation(summary = "Administrator reset password for user account")
    public ResponseEntity<Map<String, String>> adminResetPassword(
            @PathVariable Long id,
            @Valid @RequestBody AdminPasswordResetRequest request) {
        authService.adminResetPassword(id, request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Securely change authenticated user password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String username = (authentication != null && authentication.getName() != null)
                ? authentication.getName() : request.getUsername();
        authService.changePassword(username, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

    @GetMapping("/permissions")
    @Operation(summary = "Get all available system permissions, categories, and default role mappings")
    public ResponseEntity<List<PermissionResponse>> getAvailablePermissions() {
        return ResponseEntity.ok(authService.getAvailablePermissions());
    }
}
