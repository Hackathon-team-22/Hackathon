package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.AuditLog;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.domain.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

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
        log.setId(UUID.randomUUID());
        log.setEntityName("Transaction");
        log.setEntityId(tx.getId());
        User user = tx.getCreatedBy();
        log.setChangedBy(user);
        log.setTimestamp(OffsetDateTime.now());
        log.setChanges("{\"action\": \"" + action + "\"}");
        auditLogRepository.save(log);
    }
}