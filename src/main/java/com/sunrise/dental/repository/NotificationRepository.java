package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetRoleInOrTargetUsernameOrderByCreatedAtDesc(List<String> roles, String username);

    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    long countByIsReadFalse();

    long countByTargetRoleInOrTargetUsernameAndIsReadFalse(List<String> roles, String username);
}
