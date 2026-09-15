package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.entity.User;
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

import java.util.List;
import java.util.UUID;

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

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password.");
        }

        auditService.logAction(user.getUsername(), "LOGIN", "USER", user.getId().toString(), "Successful user login");

        String sessionToken = "TOKEN-" + UUID.randomUUID().toString();

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                sessionToken,
                "Authentication successful. Welcome, " + user.getFullName() + "!"
        );
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
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public User updateUserStatus(Long id, boolean active) {
        User user = getUserById(id);
        user.setIsActive(active);
        User updated = userRepository.save(user);
        auditService.logAction("ADMIN", "UPDATE_STATUS", "USER", user.getId().toString(), "Updated active status to: " + active);
        return updated;
    }
}
