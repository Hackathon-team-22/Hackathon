package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {

    /**
     * Возвращает логи, сделанные данным пользователем.
     */
    List<AuditLog> findByChangedByUserId(UUID changedByUserId);
}
