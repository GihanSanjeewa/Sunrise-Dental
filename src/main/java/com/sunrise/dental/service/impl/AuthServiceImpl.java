package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.request.UserCreateRequest;
import com.sunrise.dental.dto.request.UserUpdateRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.dto.response.PermissionResponse;
import com.sunrise.dental.dto.response.UserResponse;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.enums.Permission;
import com.sunrise.dental.enums.Role;
import com.sunrise.dental.exception.DuplicateResourceException;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.UnauthorizedException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.UserRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Please enter your username.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Please enter your password.");
        }

        String trimmedUsername = request.getUsername().trim();
        User user = userRepository.findByUsername(trimmedUsername)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password."));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Your user account has been deactivated. Please contact an administrator.");
        }

        // Account lockout check (15-minute temporary lockout after 5 consecutive failures)
        if (user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(java.time.LocalDateTime.now())) {
            throw new UnauthorizedException("Account is temporarily locked due to multiple failed login attempts. Please try again later or contact an administrator.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            int attempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setLockoutUntil(java.time.LocalDateTime.now().plusMinutes(15));
                auditService.logAction(user.getUsername(), "ACCOUNT_LOCKOUT", "USER", user.getId().toString(),
                        "Account locked for 15 minutes due to 5 consecutive failed login attempts.");
            } else {
                auditService.logAction(user.getUsername(), "FAILED_LOGIN", "USER", user.getId().toString(),
                        "Failed login attempt #" + attempts);
            }
            userRepository.save(user);
            throw new UnauthorizedException("Invalid username or password.");
        }

        // Reset failed login counter on successful authentication
        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);
        }

        auditService.logAction(user.getUsername(), "LOGIN", "USER", user.getId().toString(), "Successful user login");

        String sessionToken = "TOKEN-" + UUID.randomUUID().toString();
        Set<String> perms = user.getEffectivePermissions().stream()
                .map(Permission::name)
                .collect(Collectors.toSet());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                sessionToken,
                "Authentication successful. Welcome, " + user.getFullName() + "!",
                perms
        );
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already registered.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email '" + email + "' is already registered.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(username, hashedPassword, request.getFullName().trim(), email, request.getRole());

        Set<Permission> assignedPermissions = new HashSet<>();
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            for (String permName : request.getPermissions()) {
                try {
                    assignedPermissions.add(Permission.valueOf(permName.trim()));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } else {
            assignedPermissions.addAll(Permission.getDefaultPermissions(request.getRole()));
        }
        user.setPermissions(assignedPermissions);

        User saved = userRepository.save(user);
        auditService.logAction("ADMIN", "CREATE_USER", "USER", saved.getId().toString(),
                "Created user " + saved.getUsername() + " with role " + saved.getRole() + " and " + assignedPermissions.size() + " permissions");

        return UserResponse.fromEntity(saved);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);

        String newEmail = request.getEmail().trim();
        userRepository.findByEmail(newEmail).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Email '" + newEmail + "' is already in use by another user.");
            }
        });

        user.setFullName(request.getFullName().trim());
        user.setEmail(newEmail);
        user.setRole(request.getRole());

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getPermissions() != null) {
            Set<Permission> assignedPermissions = new HashSet<>();
            for (String permName : request.getPermissions()) {
                try {
                    assignedPermissions.add(Permission.valueOf(permName.trim()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            user.setPermissions(assignedPermissions);
        }

        User updated = userRepository.save(user);
        auditService.logAction("ADMIN", "UPDATE_USER", "USER", updated.getId().toString(),
                "Updated user " + updated.getUsername() + " profile and permissions");

        return UserResponse.fromEntity(updated);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new ValidationException("The default system administrator account cannot be deleted.");
        }

        userRepository.delete(user);
        auditService.logAction("ADMIN", "DELETE_USER", "USER", id.toString(), "Deleted user account: " + user.getUsername());
    }

    @Override
    public void unlockUser(Long id) {
        User user = getUserById(id);
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        userRepository.save(user);

        auditService.logAction("ADMIN", "UNLOCK_USER", "USER", id.toString(),
                "Unlocked user account " + user.getUsername() + " and reset failed login attempts");
    }

    @Override
    public void adminResetPassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }

        User user = getUserById(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        userRepository.save(user);

        auditService.logAction("ADMIN", "ADMIN_RESET_PASSWORD", "USER", id.toString(),
                "Administrator reset password for user: " + user.getUsername());
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditService.logAction(username, "CHANGE_PASSWORD", "USER", user.getId().toString(),
                "Password changed successfully.");
    }

    @Override
    public User registerUser(String username, String rawPassword, String fullName, String email, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already registered.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email '" + email + "' is already registered.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, hashedPassword, fullName, email, role);
        user.setPermissions(new HashSet<>(Permission.getDefaultPermissions(role)));
        User saved = userRepository.save(user);

        auditService.logAction("ADMIN", "CREATE_USER", "USER", saved.getId().toString(), "Registered user " + username + " with role " + role);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUserResponses() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserResponseById(Long id) {
        return UserResponse.fromEntity(getUserById(id));
    }

    @Override
    public User updateUserStatus(Long id, boolean active) {
        User user = getUserById(id);
        user.setIsActive(active);
        User updated = userRepository.save(user);
        auditService.logAction("ADMIN", "UPDATE_STATUS", "USER", user.getId().toString(), "Updated active status to: " + active);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAvailablePermissions() {
        return Arrays.stream(Permission.values())
                .map(PermissionResponse::fromPermission)
                .collect(Collectors.toList());
    }
}
