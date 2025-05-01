// src/main/java/com/example/finmonitor/domain/service/AuditPublisher.java
package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.AuditLog;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditPublisher {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void publishCreate(Transaction tx) {
        publishEvent(tx, "CREATED");
    }
    public void publishUpdate(Transaction tx) {
        publishEvent(tx, "UPDATED");
    }
    public void publishDelete(Transaction tx) {
        publishEvent(tx, "DELETED");
    }

    private void publishEvent(Transaction tx, String action) {
        AuditLog log = new AuditLog();
        // UUID генерится Hibernate-ом
        log.setEntityName("Transaction");
        log.setEntityId(tx.getId());
        log.setChangedByUser(tx.getCreatedByUser());
        log.setTimestamp(LocalDateTime.now());
        log.setChanges("{\"action\":\"" + action + "\"}");

        try {
            auditLogRepository.save(log);
        } catch (Exception ex) {
            // поглощаем любые ошибки аудита (например, JSONB mismatch)
            ex.printStackTrace();
        }
    }
}
