package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.request.UserCreateRequest;
import com.sunrise.dental.dto.request.UserUpdateRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.dto.response.PermissionResponse;
import com.sunrise.dental.dto.response.UserResponse;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.enums.Role;

import java.util.List;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    User registerUser(String username, String rawPassword, String fullName, String email, Role role);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void unlockUser(Long id);
    void adminResetPassword(Long id, String newPassword);
    List<User> getAllUsers();
    List<UserResponse> getAllUserResponses();
    User getUserById(Long id);
    UserResponse getUserResponseById(Long id);
    User updateUserStatus(Long id, boolean active);
    void changePassword(String username, String currentPassword, String newPassword);
    List<PermissionResponse> getAvailablePermissions();
}
