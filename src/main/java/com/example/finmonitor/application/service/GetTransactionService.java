package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис получения транзакции по идентификатору
 */
@Service
public class GetTransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Возвращает транзакцию по ID или выбрасывает IllegalArgumentException, если не найдено
     */
    public Transaction execute(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Transaction with id %s not found", id)
                ));
    }
}
