package com.example.finmonitor.api;

import com.example.finmonitor.domain.model.*;
import com.example.finmonitor.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TestReferenceDataHelper {

    @Autowired private PartyTypeRepository partyTypeRepository;
    @Autowired private TransactionTypeRepository transactionTypeRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private BankRepository bankRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getOrCreatePartyType(String name) {
        return partyTypeRepository.getOrCreateByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getOrCreateTransactionType(String name) {
        return transactionTypeRepository.getOrCreateByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getOrCreateStatus(String name) {
        return statusRepository.getOrCreateByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getOrCreateBank(String name) {
        return bankRepository.getOrCreateByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getOrCreateCategory(String name) {
        return categoryRepository.getOrCreateByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getOrCreateUser(String username, String encodedPassword, String role) {
        return userRepository.findByUsername(username) != null ?
                userRepository.findByUsername(username).getId() :
                userRepository.save(new User(UUID.randomUUID(), username, encodedPassword, role)).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID createTransaction(
            UUID partyTypeId,
            UUID transactionTypeId,
            UUID statusId,
            UUID bankSenderId,
            UUID bankReceiverId,
            UUID categoryId,
            String createdByUsername
    ) {
        User existingUser = userRepository.findByUsername(createdByUsername);
        if (existingUser == null) throw new IllegalStateException("User not found: " + createdByUsername);

        Transaction tx = new Transaction();
        tx.setCreatedByUser(existingUser);
        tx.setTimestamp(LocalDateTime.now());

        tx.setPartyType(partyTypeRepository.findById(partyTypeId).orElseThrow());
        tx.setTransactionType(transactionTypeRepository.findById(transactionTypeId).orElseThrow());
        tx.setStatus(statusRepository.findById(statusId).orElseThrow());
        tx.setBankSender(bankRepository.findById(bankSenderId).orElseThrow());
        tx.setBankReceiver(bankRepository.findById(bankReceiverId).orElseThrow());
        tx.setCategory(categoryRepository.findById(categoryId).orElseThrow());

        tx.setAccountSender("40817810000000000001");
        tx.setAccountReceiver("40817810000000000002");
        tx.setAmount(new BigDecimal("1000.00"));
        tx.setReceiverTin("12345678901");
        tx.setReceiverPhone("89991234567");
        tx.setComment("Test transaction");

        return transactionRepository.save(tx).getId();
    }
}
