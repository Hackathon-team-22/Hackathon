package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterTransactionsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FilterTransactionsService filterService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExecute_withFilters() {
        Transaction tx = new Transaction();
        Page<Transaction> page = new PageImpl<>(List.of(tx));
        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Transaction> result = filterService.execute(
                UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(),
                UUID.randomUUID(), "12345678901", new BigDecimal("1"), new BigDecimal("10"),
                UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), // userId filter
                Pageable.unpaged()
        );

        assertSame(page, result);
        verify(transactionRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
