package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.entity.User;

import java.util.List;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    User registerUser(String username, String rawPassword, String fullName, String email, com.sunrise.dental.enums.Role role);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUserStatus(Long id, boolean active);
    void changePassword(String username, String currentPassword, String newPassword);
}
