package com.sunrise.dental.controller;

import com.sunrise.dental.dto.response.NotificationResponse;
import com.sunrise.dental.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification System", description = "In-app notifications, low stock alerts, and appointment reminders")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get in-app notifications for authenticated user")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        String role = extractRole(authentication);
        return ResponseEntity.ok(notificationService.getNotificationsForUser(role, username));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get count of unread notifications for current user")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        String role = extractRole(authentication);
        long count = notificationService.getUnreadCount(role, username);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark single notification as read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read."));
    }

    @PatchMapping("/mark-all-read")
    @Operation(summary = "Mark all notifications for current user as read")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        String role = extractRole(authentication);
        notificationService.markAllAsRead(role, username);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read."));
    }

    @PostMapping("/generate-alerts")
    @Operation(summary = "Trigger background system check for low inventory and pending alerts")
    public ResponseEntity<Map<String, String>> generateAlerts() {
        notificationService.generateSystemAlerts();
        return ResponseEntity.ok(Map.of("message", "System alerts generated successfully."));
    }

    private String extractRole(Authentication authentication) {
        if (authentication == null) return null;
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .orElse(null);
    }
}
