package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.repository.StatusRepository;
import com.example.finmonitor.repository.TransactionRepository;
import com.example.finmonitor.domain.service.AuditPublisher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class DeleteTransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private StatusRepository statusRepository;
    @Autowired private AuditPublisher auditPublisher;

    // Статусы, при которых удаление запрещено
    private static final Set<String> nonDeletableStatuses = Set.of("Completed", "Cancelled");

    /**
     * Помечает транзакцию удалённой только если она принадлежит пользователю userId.
     */
    @Transactional
    public void execute(UUID transactionId, UUID userId) {
        Transaction existingTx = transactionRepository
                .findByIdAndCreatedByUserId(transactionId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Транзакция не найдена или недоступна"));

        if (nonDeletableStatuses.contains(existingTx.getStatus().getName())) {
            throw new IllegalStateException(
                    "Транзакция с данным статусом не может быть удалена"
            );
        }

        Status deletedStatus = statusRepository.findByName("Deleted")
                .orElseThrow(() ->
                        new IllegalArgumentException("Статус 'Deleted' не найден"));

        existingTx.setStatus(deletedStatus);
        transactionRepository.save(existingTx);
        auditPublisher.publishDelete(existingTx);
    }
}
