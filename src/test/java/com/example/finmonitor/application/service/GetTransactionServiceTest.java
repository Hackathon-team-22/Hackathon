package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class GetTransactionServiceTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");


    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTransactionService getService;

    private UUID id;
    private Transaction tx;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        tx = new Transaction();
        tx.setId(id);
        when(transactionRepository.findByIdAndCreatedByUserId(id, USER_ID)).thenReturn(Optional.of(tx));
    }

    @Test
    public void execute_success() {
        Transaction result = getService.execute(id, USER_ID);
        assertEquals(tx, result);
    }

    @Test
    public void execute_notFound_throws() {
        UUID otherId = UUID.randomUUID();
        when(transactionRepository.findByIdAndCreatedByUserId(otherId, USER_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> getService.execute(otherId, USER_ID));
    }
}