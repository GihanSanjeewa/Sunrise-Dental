package com.sunrise.dental.dto.response;

import com.sunrise.dental.entity.User;
import com.sunrise.dental.enums.Permission;
import com.sunrise.dental.enums.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isActive;
    private Integer failedLoginAttempts;
    private Boolean isLocked;
    private LocalDateTime lockoutUntil;
    private Set<String> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String fullName, String email, Role role,
                        Boolean isActive, Integer failedLoginAttempts, Boolean isLocked,
                        LocalDateTime lockoutUntil, Set<String> permissions,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.failedLoginAttempts = failedLoginAttempts;
        this.isLocked = isLocked;
        this.lockoutUntil = lockoutUntil;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        boolean locked = user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(LocalDateTime.now());
        Set<String> perms = user.getEffectivePermissions().stream()
                .map(Permission::name)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0,
                locked,
                user.getLockoutUntil(),
                perms,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    public LocalDateTime getLockoutUntil() {
        return lockoutUntil;
    }

    public void setLockoutUntil(LocalDateTime lockoutUntil) {
        this.lockoutUntil = lockoutUntil;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
