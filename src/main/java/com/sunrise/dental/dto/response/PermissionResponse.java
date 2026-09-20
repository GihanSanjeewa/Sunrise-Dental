package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.Permission;
import com.sunrise.dental.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class PermissionResponse {

    private String code;
    private String name;
    private String category;
    private String description;
    private List<String> defaultRoles;

    public PermissionResponse() {
    }

    public PermissionResponse(String code, String name, String category, String description, List<String> defaultRoles) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.description = description;
        this.defaultRoles = defaultRoles;
    }

    public static PermissionResponse fromPermission(Permission p) {
        List<String> roles = new ArrayList<>();
        for (Role role : Role.values()) {
            if (Permission.getDefaultPermissions(role).contains(p)) {
                roles.add(role.name());
            }
        }

        // Format friendly name from enum: PATIENT_READ -> Patient Read
        String[] parts = p.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
        }

        return new PermissionResponse(
                p.name(),
                sb.toString(),
                p.getCategory(),
                p.getDescription(),
                roles
        );
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDefaultRoles() {
        return defaultRoles;
    }

    public void setDefaultRoles(List<String> defaultRoles) {
        this.defaultRoles = defaultRoles;
    }
}
