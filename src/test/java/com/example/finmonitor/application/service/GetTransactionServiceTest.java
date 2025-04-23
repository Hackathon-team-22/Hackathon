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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTransactionService getService;

    private UUID id;
    private Transaction tx;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        tx = new Transaction();
        tx.setId(id);
    }

    @Test
    void execute_found_returnsTransaction() {
        when(transactionRepository.findById(id)).thenReturn(Optional.of(tx));

        Transaction result = getService.execute(id);

        assertSame(tx, result);
        verify(transactionRepository).findById(id);
    }

    @Test
    void execute_notFound_throws() {
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> getService.execute(id));
        assertTrue(ex.getMessage().contains(id.toString()));
    }
}