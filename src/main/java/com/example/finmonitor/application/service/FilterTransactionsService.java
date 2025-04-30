package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.repository.TransactionRepository;
import com.example.finmonitor.domain.spec.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FilterTransactionsService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Возвращает страницу транзакций текущего пользователя с заданными фильтрами.
     */
    public Page<Transaction> execute(
            UUID bankSenderId,
            UUID bankReceiverId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            UUID statusId,
            String receiverTin,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            UUID transactionTypeId,
            UUID categoryId,
            UUID userId,
            Pageable pageable
    ) {
        return transactionRepository.findAll(
                TransactionSpecification.withFilters(
                        bankSenderId,
                        bankReceiverId,
                        fromDate,
                        toDate,
                        statusId,
                        receiverTin,
                        minAmount,
                        maxAmount,
                        transactionTypeId,
                        categoryId,
                        userId
                ),
                pageable
        );
    }
}
