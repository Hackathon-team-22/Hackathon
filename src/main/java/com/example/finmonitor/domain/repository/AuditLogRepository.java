package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}