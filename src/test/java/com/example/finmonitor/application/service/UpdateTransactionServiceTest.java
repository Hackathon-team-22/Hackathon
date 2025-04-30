package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.repository.StatusRepository;
import com.example.finmonitor.repository.TransactionRepository;
import com.example.finmonitor.domain.service.AuditPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateTransactionServiceTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private AuditPublisher auditPublisher;

    @InjectMocks
    private UpdateTransactionService updateService;

    private Transaction existing;
    private UUID id;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        existing = new Transaction();
        existing.setId(id);
        existing.setStatus(new Status(UUID.randomUUID(), "Pending"));
        when(transactionRepository.findByIdAndCreatedByUserId(id, USER_ID)).thenReturn(Optional.of(existing));
    }


    @Test
    void execute_nonEditable_throws() {
        existing.setStatus(new Status(UUID.randomUUID(), "Completed"));
        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> {
            Transaction updated = new Transaction();
            updated.setStatus(new Status(UUID.randomUUID(), "Completed"));
            updateService.execute(id, updated, USER_ID);
        });
    }
}
