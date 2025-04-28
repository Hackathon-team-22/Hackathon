package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Возвращает транзакцию только если она принадлежит пользователю userId.
     */
    public Transaction execute(UUID transactionId, UUID userId) {
        return transactionRepository
                .findByIdAndCreatedByUserId(transactionId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Транзакция не найдена или недоступна"));
    }
}
