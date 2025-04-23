package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.*;
import com.example.finmonitor.domain.repository.*;
import com.example.finmonitor.domain.service.AuditPublisher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UpdateTransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private AuditPublisher auditPublisher;

    private final Set<String> immutableStatuses = Set.of(
            "Подтвержденная", "В обработке", "Отменена", "Платеж выполнен", "Платеж удален", "Возврат");

    @Transactional
    public Transaction execute(UUID transactionId, Transaction updatedData) {
        Transaction existingTx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction не найдена"));

        if (immutableStatuses.contains(existingTx.getStatus().getName())) {
            throw new IllegalStateException("Транзакция с данным статусом не может быть изменена");
        }

        // Обновляем допустимые для редактирования поля
        existingTx.setPartyType(updatedData.getPartyType());
        existingTx.setTimestamp(updatedData.getTimestamp());
        existingTx.setComment(updatedData.getComment());
        existingTx.setAmount(updatedData.getAmount());
        existingTx.setStatus(updatedData.getStatus());
        existingTx.setBankSender(updatedData.getBankSender());
        existingTx.setBankReceiver(updatedData.getBankReceiver());
        existingTx.setReceiverTin(updatedData.getReceiverTin());
        existingTx.setCategory(updatedData.getCategory());
        existingTx.setReceiverPhone(updatedData.getReceiverPhone());

        Transaction savedTx = transactionRepository.save(existingTx);
        auditPublisher.publishUpdate(savedTx);

        return savedTx;
    }
}