package com.sunrise.dental.service.impl;

import com.sunrise.dental.entity.AuditLog;
import com.sunrise.dental.repository.AuditLogRepository;
import com.sunrise.dental.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logAction(String username, String action, String entityName, String entityId, String details) {
        logAction(username, action, entityName, entityId, details, null, null, null, null);
    }

    @Override
    public void logAction(String username, String action, String entityName, String entityId, String details,
                          String oldValue, String newValue, String ipAddress, String userAgent) {
        try {
            AuditLog log = new AuditLog(
                    username != null ? username : "SYSTEM",
                    action,
                    entityName,
                    entityId,
                    details,
                    oldValue,
                    newValue,
                    ipAddress,
                    userAgent
            );
            auditLogRepository.save(log);
            logger.info("AUDIT [{}] - {} on {} [ID: {}]: {}", username, action, entityName, entityId, details);
        } catch (Exception e) {
            logger.error("Failed to persist audit log: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
