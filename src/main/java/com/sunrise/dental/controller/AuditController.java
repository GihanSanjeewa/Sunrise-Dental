package com.sunrise.dental.controller;

import com.sunrise.dental.entity.AuditLog;
import com.sunrise.dental.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "System Audit Logging", description = "Immutable audit ledger for system actions and security governance")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(summary = "Get recent audit trail logs (Admin only)")
    public ResponseEntity<List<AuditLog>> getRecentLogs() {
        return ResponseEntity.ok(auditService.getRecentLogs());
    }
}
