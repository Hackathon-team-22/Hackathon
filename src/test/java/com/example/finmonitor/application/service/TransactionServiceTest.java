package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.StatusRepository;
import com.example.finmonitor.domain.repository.TransactionRepository;
import com.example.finmonitor.domain.service.AuditPublisher;
import com.example.finmonitor.domain.service.StatusMachine;
import com.example.finmonitor.domain.service.TransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private TransactionValidator validator;
    @Mock
    private StatusMachine statusMachine;
    @Mock
    private AuditPublisher auditPublisher;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_validTransaction_persistsAndPublishes() {
        Transaction tx = new Transaction();
        tx.setReceiverTin("12345678901");
        tx.setReceiverPhone("81234567890");
        when(transactionRepository.save(tx)).thenReturn(tx);

        Transaction result = service.create(tx);

        assertThat(result).isSameAs(tx);
        verify(validator).validateTIN(tx.getReceiverTin());
        verify(validator).validatePhone(tx.getReceiverPhone());
        verify(auditPublisher).publishCreate(tx);
    }

    @Test
    void create_invalidTin_throws() {
        Transaction tx = new Transaction();
        tx.setReceiverTin("bad");
        tx.setReceiverPhone("81234567890");
        doThrow(new IllegalArgumentException()).when(validator).validateTIN(any());

        assertThrows(IllegalArgumentException.class, () -> service.create(tx));
        verify(auditPublisher, never()).publishCreate(any());
    }

    @Test
    void update_nonexistentTransaction_throws() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.update(id, new Transaction()));
    }

    @Test
    void delete_nonexistentTransaction_throws() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.delete(id));
    }

    @Test
    void list_noFilters_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of());
        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);


        Page<Transaction> result = service.list(null, null, null, null, null, null, null, null, null, null, pageable);
        assertThat(result).isSameAs(page);
    }
}