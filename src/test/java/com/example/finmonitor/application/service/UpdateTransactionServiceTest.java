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

class UpdateTransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private StatusRepository statusRepository;
    @Mock private AuditPublisher auditPublisher;

    @InjectMocks
    private UpdateTransactionService updateService;

    private UUID id;
    private Transaction existing;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        existing = new Transaction();
        existing.setId(id);
        existing.setStatus(new Status(UUID.randomUUID(), "New"));
    }

    @Test
    void execute_editableStatus_updatesAndPublishes() {
        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));
//        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(UUID.randomUUID()); // Установка ID вручную для проверки
            return t;
        });
        Transaction updated = new Transaction();
        updated.setStatus(new Status(UUID.randomUUID(), "New"));
        Transaction result = updateService.execute(id, updated);

        assertSame(existing, result);
        verify(auditPublisher).publishUpdate(existing);
    }

    @Test
    void execute_nonEditable_throws() {
        existing.setStatus(new Status(UUID.randomUUID(), "Подтвержденная"));
        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> {
            Transaction updated = new Transaction();
            updated.setStatus(new Status(UUID.randomUUID(), "Подтвержденная"));
            updateService.execute(id, updated);
        });
    }
}