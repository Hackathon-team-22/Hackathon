package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.repository.StatusRepository;
import com.example.finmonitor.domain.repository.TransactionRepository;
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

    // Статусы, при которых правка запрещена
    private static final Set<String> immutableStatuses = Set.of("Completed", "Cancelled");

    /**
     * Обновляет транзакцию только если она принадлежит пользователю userId.
     */
    @Transactional
    public Transaction execute(UUID transactionId, Transaction updatedData, UUID userId) {
        Transaction existingTx = transactionRepository
                .findByIdAndCreatedByUserId(transactionId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Транзакция не найдена или недоступна"));

        if (immutableStatuses.contains(existingTx.getStatus().getName())) {
            throw new IllegalStateException(
                    "Транзакция со статусом '" + existingTx.getStatus().getName() + "' не может быть отредактирована"
            );
        }

        // Обновляем только разрешённые поля
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

        // Сохраняем и публикуем в аудит
        Transaction savedTx = transactionRepository.save(existingTx);
        auditPublisher.publishUpdate(savedTx);

        return savedTx;
    }
}
