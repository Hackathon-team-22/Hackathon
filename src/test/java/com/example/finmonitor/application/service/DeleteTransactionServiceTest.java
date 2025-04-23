package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.StatusRepository;
import com.example.finmonitor.domain.repository.TransactionRepository;
import com.example.finmonitor.domain.service.AuditPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteTransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private StatusRepository statusRepository;
    @Mock private AuditPublisher auditPublisher;

    @InjectMocks
    private DeleteTransactionService deleteService;

    private UUID id;
    private Transaction existing;
    private Status deletedStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        existing = new Transaction();
        existing.setId(id);
        existing.setStatus(new Status(UUID.randomUUID(), "Новая"));

        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));
        deletedStatus = new Status(UUID.randomUUID(), "Платеж удален");
        when(statusRepository.findByName("Платеж удален")).thenReturn(Optional.of(deletedStatus));
    }

    @Test
    void execute_deletable_setsDeletedStatusAndPublishes() {
        deleteService.execute(id);
        assertEquals(deletedStatus, existing.getStatus());
        verify(transactionRepository).save(existing);
        verify(auditPublisher).publishDelete(existing);
    }

    @Test
    void execute_nonDeletable_throws() {
        existing.setStatus(new Status(UUID.randomUUID(), "Подтвержденная"));
        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> deleteService.execute(id));
    }
}