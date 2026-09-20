package com.sunrise.dental.service;

import com.sunrise.dental.dto.response.NotificationResponse;
import com.sunrise.dental.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(String title, String message, NotificationType type,
                                            String targetRole, String targetUsername, String linkUrl);

    List<NotificationResponse> getNotificationsForUser(String role, String username);

    List<NotificationResponse> getUnreadNotifications();

    long getUnreadCount(String role, String username);

    void markAsRead(Long id);

    void markAllAsRead(String role, String username);

    void generateSystemAlerts();
}
