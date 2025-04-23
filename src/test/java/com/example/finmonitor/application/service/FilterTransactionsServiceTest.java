package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterTransactionsServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private FilterTransactionsService filterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_callsRepositoryAndReturnsPage() {
        Transaction tx = new Transaction();
        Page<Transaction> page = new PageImpl<>(List.of(tx));
        when(transactionRepository.filterTransactions(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        Page<Transaction> result = filterService.execute(
                UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(),
                UUID.randomUUID(), "12345678901", new BigDecimal("1"), new BigDecimal("10"),
                UUID.randomUUID(), UUID.randomUUID(), Pageable.unpaged()
        );

        assertSame(page, result);
        verify(transactionRepository).filterTransactions(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)
        );
    }
}