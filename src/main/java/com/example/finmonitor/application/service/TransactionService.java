package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.repository.TransactionRepository;
import com.example.finmonitor.domain.repository.StatusRepository;
import com.example.finmonitor.domain.service.TransactionValidator;
import com.example.finmonitor.domain.service.StatusMachine;
import com.example.finmonitor.domain.service.AuditPublisher;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StatusRepository statusRepository;
    private final TransactionValidator validator;
    private final StatusMachine statusMachine;
    private final AuditPublisher auditPublisher;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            StatusRepository statusRepository,
            TransactionValidator validator,
            StatusMachine statusMachine,
            AuditPublisher auditPublisher) {
        this.transactionRepository = transactionRepository;
        this.statusRepository = statusRepository;
        this.validator = validator;
        this.statusMachine = statusMachine;
        this.auditPublisher = auditPublisher;
    }

    /**
     * Получить транзакцию по ID.
     */
    public Transaction getById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    @Transactional
    public Transaction create(Transaction tx) {
        validator.validateTIN(tx.getReceiverTin());
        validator.validatePhone(tx.getReceiverPhone());
        Transaction saved = transactionRepository.save(tx);
        auditPublisher.publishCreate(saved);
        return saved;
    }

    @Transactional
    public Transaction update(UUID id, Transaction updated) {
        Transaction existing = getById(id);
        validator.checkEditable(existing.getStatus());
        Status newStatus = statusRepository.findById(updated.getStatus().getId())
                .orElseThrow(() -> new IllegalArgumentException("Status not found"));
        statusMachine.validateTransition(existing.getStatus(), newStatus);

        existing.setPartyType(updated.getPartyType());
        existing.setTimestamp(updated.getTimestamp());
        existing.setComment(updated.getComment());
        existing.setAmount(updated.getAmount());
        existing.setStatus(newStatus);
        existing.setBankSender(updated.getBankSender());
        existing.setBankReceiver(updated.getBankReceiver());
        existing.setReceiverTin(updated.getReceiverTin());
        existing.setCategory(updated.getCategory());
        existing.setReceiverPhone(updated.getReceiverPhone());

        validator.validateTIN(existing.getReceiverTin());
        validator.validatePhone(existing.getReceiverPhone());

        Transaction saved = transactionRepository.save(existing);
        auditPublisher.publishUpdate(saved);
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        Transaction tx = getById(id);
        validator.checkEditable(tx.getStatus());
        Status deletedStatus = statusRepository.findByName("Deleted")
                .orElseThrow(() -> new IllegalStateException("Deleted status not found"));
        tx.setStatus(deletedStatus);
        transactionRepository.save(tx);
        auditPublisher.publishDelete(tx);
    }

    public Page<Transaction> list(
            UUID bankSenderId,
            UUID bankReceiverId,
            String dateFrom,
            String dateTo,
            UUID statusId,
            String tin,
            BigDecimal amountMin,
            BigDecimal amountMax,
            UUID transactionTypeId,
            UUID categoryId,
            Pageable pageable) {
        Specification<Transaction> spec = Specification.where(null);

        spec = spec.and((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (bankSenderId != null) {
                predicates.add(cb.equal(root.get("bankSender").get("id"), bankSenderId));
            }
            if (bankReceiverId != null) {
                predicates.add(cb.equal(root.get("bankReceiver").get("id"), bankReceiverId));
            }
            if (statusId != null) {
                predicates.add(cb.equal(root.get("status").get("id"), statusId));
            }
            if (transactionTypeId != null) {
                predicates.add(cb.equal(root.get("transactionType").get("id"), transactionTypeId));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (tin != null) {
                predicates.add(cb.equal(root.get("receiverTin"), tin));
            }
            if (amountMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), amountMin));
            }
            if (amountMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), amountMax));
            }
            if (dateFrom != null) {
                LocalDate fromDate = LocalDate.parse(dateFrom, DATE_FORMATTER);
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("timestamp"),
                        fromDate.atStartOfDay().atOffset(OffsetDateTime.now().getOffset())));
            }
            if (dateTo != null) {
                LocalDate toDate = LocalDate.parse(dateTo, DATE_FORMATTER);
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("timestamp"),
                        toDate.atTime(23, 59, 59).atOffset(OffsetDateTime.now().getOffset())));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        return transactionRepository.findAll(spec, pageable);
    }
}
