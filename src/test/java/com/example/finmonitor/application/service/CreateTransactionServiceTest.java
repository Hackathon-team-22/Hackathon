package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.*;
import com.example.finmonitor.domain.repository.*;
import com.example.finmonitor.domain.service.AuditPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateTransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private PartyTypeRepository partyTypeRepository;
    @Mock private TransactionTypeRepository transactionTypeRepository;
    @Mock private StatusRepository statusRepository;
    @Mock private BankRepository bankRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AuditPublisher auditPublisher;

    @InjectMocks
    private CreateTransactionService createService;

    private UUID userId, ptId, ttId, stId, bsId, brId, catId;
    private Transaction tx;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        ptId   = UUID.randomUUID();
        ttId   = UUID.randomUUID();
        stId   = UUID.randomUUID();
        bsId   = UUID.randomUUID();
        brId   = UUID.randomUUID();
        catId  = UUID.randomUUID();

        tx = new Transaction();
        tx.setPartyType(new PartyType(ptId, null));
        tx.setTransactionType(new TransactionType(ttId, null));
        tx.setStatus(new Status(stId, null));
        tx.setBankSender(new Bank(bsId, null));
        tx.setBankReceiver(new Bank(brId, null));
        tx.setAccountSender("acc1");
        tx.setAccountReceiver("acc2");
        tx.setCategory(new Category(catId, null));
        tx.setAmount(new BigDecimal("123.45"));
        tx.setReceiverTin("12345678901");
        tx.setReceiverPhone("81234567890");
        tx.setComment("test");
    }

    @Test
    void execute_allRefsExist_savesAndPublishes() {
        user = new User(); user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(partyTypeRepository.findById(ptId)).thenReturn(Optional.of(new PartyType(ptId, null)));
        when(transactionTypeRepository.findById(ttId)).thenReturn(Optional.of(new TransactionType(ttId, null)));
        when(statusRepository.findById(stId)).thenReturn(Optional.of(new Status(stId, null)));
        when(bankRepository.findById(bsId)).thenReturn(Optional.of(new Bank(bsId, null)));
        when(bankRepository.findById(brId)).thenReturn(Optional.of(new Bank(brId, null)));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(new Category(catId, null)));
//        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        when(transactionRepository.save(any())).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(UUID.randomUUID()); // Установка ID вручную для проверки
            return t;
        });

        Transaction result = createService.execute(tx, userId);

        assertNotNull(result.getId());
        assertEquals(user, result.getCreatedByUser());
        verify(transactionRepository).save(result);
        verify(auditPublisher).publishCreate(result);
    }

    @Test
    void execute_missingUser_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createService.execute(tx, userId));
        assertTrue(ex.getMessage().contains("Пользователь не найден"));
    }
}