package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.*;
import com.example.finmonitor.domain.service.AuditPublisher;
import com.example.finmonitor.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateTransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PartyTypeRepository partyTypeRepository;
    @Autowired private TransactionTypeRepository transactionTypeRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private BankRepository bankRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AuditPublisher auditPublisher;

    @Transactional
    public Transaction execute(Transaction transaction, UUID createdByUserId) {
        // Проверка и подготовка данных
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        PartyType partyType = partyTypeRepository.findById(transaction.getPartyType().getId())
                .orElseThrow(() -> new IllegalArgumentException("PartyType не найден"));

        TransactionType txType = transactionTypeRepository.findById(transaction.getTransactionType().getId())
                .orElseThrow(() -> new IllegalArgumentException("TransactionType не найден"));

        Status status = statusRepository.findById(transaction.getStatus().getId())
                .orElseThrow(() -> new IllegalArgumentException("Status не найден"));

        Bank bankSender = bankRepository.findById(transaction.getBankSender().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender Bank не найден"));

        Bank bankReceiver = bankRepository.findById(transaction.getBankReceiver().getId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver Bank не найден"));

        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category не найдена"));

        // Устанавливаем проверенные значения
        transaction.setCreatedByUser(createdBy);
        transaction.setPartyType(partyType);
        transaction.setTransactionType(txType);
        transaction.setStatus(status);
        transaction.setBankSender(bankSender);
        transaction.setBankReceiver(bankReceiver);
        transaction.setCategory(category);
        transaction.setTimestamp(LocalDateTime.now());

        // Сохраняем транзакцию
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Публикуем событие в аудит
        auditPublisher.publishCreate(savedTransaction);

        return savedTransaction;
    }
}
