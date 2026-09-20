package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.Role;
import java.util.Set;

public class AuthResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private String token;
    private String message;
    private Set<String> permissions;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String username, String fullName, String email, Role role, String token, String message) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.message = message;
    }

    public AuthResponse(Long id, String username, String fullName, String email, Role role, String token, String message, Set<String> permissions) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.message = message;
        this.permissions = permissions;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
