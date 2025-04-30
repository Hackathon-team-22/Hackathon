package com.example.finmonitor.application.service;

import com.example.finmonitor.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class GenerateDashboardDataService {

    @Autowired private TransactionRepository transactionRepository;

    public Map<String, Object> execute(LocalDate fromDate, LocalDate toDate) {
        long totalTransactions = transactionRepository.countByTimestampBetween(fromDate, toDate);
        long completedTransactions = transactionRepository.countByStatusNameAndTimestampBetween("Платеж выполнен", fromDate, toDate);
        long cancelledTransactions = transactionRepository.countByStatusNameAndTimestampBetween("Отменена", fromDate, toDate);

        return Map.of(
                "totalTransactions", totalTransactions,
                "completedTransactions", completedTransactions,
                "cancelledTransactions", cancelledTransactions
        );
    }
}
