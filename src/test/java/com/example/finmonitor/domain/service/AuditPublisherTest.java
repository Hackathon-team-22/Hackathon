package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.AuditLog;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class AuditPublisherTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditPublisher publisher;

    @Captor
    private ArgumentCaptor<AuditLog> logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publishCreate_savesAuditLog() {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        User user = new User(); user.setId(UUID.randomUUID());
        tx.setCreatedByUser(user);

        publisher.publishCreate(tx);

        verify(auditLogRepository).save(logCaptor.capture());
        AuditLog log = logCaptor.getValue();
        assertThat(log.getEntityName()).isEqualTo("Transaction");
        assertThat(log.getEntityId()).isEqualTo(tx.getId());
        assertThat(log.getChangedByUser()).isEqualTo(user);
        assertThat(log.getChanges()).contains("CREATED");
    }

    @Test
    void publishUpdate_savesAuditLogWithUpdatedAction() {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        User user = new User(); user.setId(UUID.randomUUID());
        tx.setCreatedByUser(user);

        publisher.publishUpdate(tx);

        verify(auditLogRepository).save(logCaptor.capture());
        AuditLog log = logCaptor.getValue();
        assertThat(log.getChanges()).contains("UPDATED");
    }

    @Test
    void publishDelete_savesAuditLogWithDeletedAction() {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        User user = new User(); user.setId(UUID.randomUUID());
        tx.setCreatedByUser(user);

        publisher.publishDelete(tx);

        verify(auditLogRepository).save(logCaptor.capture());
        AuditLog log = logCaptor.getValue();
        assertThat(log.getChanges()).contains("DELETED");
    }
}
