package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FilterTransactionsService {

    @Autowired private TransactionRepository transactionRepository;

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
            Pageable pageable
    ) {
        return transactionRepository.filterTransactions(
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
                pageable
        );
    }
}