package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.response.NotificationResponse;
import com.sunrise.dental.entity.InventoryItem;
import com.sunrise.dental.entity.Notification;
import com.sunrise.dental.enums.NotificationType;
import com.sunrise.dental.enums.PaymentStatus;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.BillRepository;
import com.sunrise.dental.repository.InventoryItemRepository;
import com.sunrise.dental.repository.NotificationRepository;
import com.sunrise.dental.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   InventoryItemRepository inventoryItemRepository,
                                   AppointmentRepository appointmentRepository,
                                   BillRepository billRepository) {
        this.notificationRepository = notificationRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    @Override
    public NotificationResponse createNotification(String title, String message, NotificationType type,
                                                    String targetRole, String targetUsername, String linkUrl) {
        Notification notification = new Notification(
                title,
                message,
                type,
                targetRole != null ? targetRole : "ALL",
                targetUsername,
                linkUrl
        );

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(String role, String username) {
        List<String> roles = Arrays.asList("ALL", role != null ? role.toUpperCase() : "ALL");
        return notificationRepository.findByTargetRoleInOrTargetUsernameOrderByCreatedAtDesc(roles, username)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String role, String username) {
        List<String> roles = Arrays.asList("ALL", role != null ? role.toUpperCase() : "ALL");
        return notificationRepository.countByTargetRoleInOrTargetUsernameAndIsReadFalse(roles, username);
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void markAllAsRead(String role, String username) {
        List<String> roles = Arrays.asList("ALL", role != null ? role.toUpperCase() : "ALL");
        List<Notification> list = notificationRepository.findByTargetRoleInOrTargetUsernameOrderByCreatedAtDesc(roles, username);
        for (Notification n : list) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(list);
    }

    @Override
    public void generateSystemAlerts() {
        // 1. Low stock alerts
        List<InventoryItem> lowStock = inventoryItemRepository.findLowStockItems();
        for (InventoryItem item : lowStock) {
            String title = "Low Stock: " + item.getName();
            boolean exists = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc().stream()
                    .anyMatch(n -> n.getTitle().equals(title));
            if (!exists) {
                createNotification(
                        title,
                        item.getName() + " has only " + item.getCurrentQuantity() + " " + item.getUnit() + " remaining (Minimum: " + item.getMinimumQuantity() + ").",
                        NotificationType.LOW_STOCK,
                        "ADMIN",
                        null,
                        "inventory.html"
                );
            }
        }

        // 2. Outstanding payment notice
        long pendingBillsCount = billRepository.findByPaymentStatus(PaymentStatus.PENDING).size();
        if (pendingBillsCount > 0) {
            boolean exists = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc().stream()
                    .anyMatch(n -> n.getType() == NotificationType.OUTSTANDING_PAYMENT);
            if (!exists) {
                createNotification(
                        "Outstanding Receivables",
                        "There are " + pendingBillsCount + " unpaid invoices awaiting settlement.",
                        NotificationType.OUTSTANDING_PAYMENT,
                        "RECEPTIONIST",
                        null,
                        "billing.html"
                );
            }
        }

        // 3. Today's appointments notice
        long todayCount = appointmentRepository.countByAppointmentDate(LocalDate.now());
        if (todayCount > 0) {
            boolean exists = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc().stream()
                    .anyMatch(n -> n.getType() == NotificationType.APPOINTMENT_UPCOMING && n.getMessage().contains("Today's schedule"));
            if (!exists) {
                createNotification(
                        "Today's Appointments",
                        "Today's schedule has " + todayCount + " patient visits.",
                        NotificationType.APPOINTMENT_UPCOMING,
                        "ALL",
                        null,
                        "appointments.html"
                );
            }
        }
    }

    private NotificationResponse mapToResponse(Notification n) {
        NotificationResponse resp = new NotificationResponse();
        resp.setId(n.getId());
        resp.setTitle(n.getTitle());
        resp.setMessage(n.getMessage());
        resp.setType(n.getType());
        resp.setTargetRole(n.getTargetRole());
        resp.setTargetUsername(n.getTargetUsername());
        resp.setIsRead(n.getIsRead());
        resp.setLinkUrl(n.getLinkUrl());
        resp.setCreatedAt(n.getCreatedAt());
        return resp;
    }
}
