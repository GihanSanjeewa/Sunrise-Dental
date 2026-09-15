package com.sunrise.dental.service;

import com.sunrise.dental.entity.AuditLog;
import java.util.List;

public interface AuditService {
    void logAction(String username, String action, String entityName, String entityId, String details);
    List<AuditLog> getRecentLogs();
}
