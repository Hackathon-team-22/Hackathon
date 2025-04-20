package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.repository.StatusRepository;
import com.example.finmonitor.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionValidator validator;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private AuditPublisher auditPublisher;

    @Transactional
    public Transaction create(Transaction tx) {
        // validate fields
        validator.validateDate(tx.getTimestamp().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        validator.validateTIN(tx.getReceiverTin());
        validator.validatePhone(tx.getReceiverPhone());
        // init and save
        tx.setId(UUID.randomUUID());
        Transaction saved = transactionRepository.save(tx);
        auditPublisher.publishCreate(saved);
        return saved;
    }

    @Transactional
    public Transaction update(UUID id, Transaction updates) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        // check editable
        validator.checkEditable(existing.getStatus());
        // apply allowed updates
        existing.setPartyType(updates.getPartyType());
        existing.setTimestamp(updates.getTimestamp());
        existing.setComment(updates.getComment());
        existing.setAmount(updates.getAmount());
        existing.setStatus(updates.getStatus());
        existing.setBankSender(updates.getBankSender());
        existing.setBankReceiver(updates.getBankReceiver());
        existing.setReceiverTin(updates.getReceiverTin());
        existing.setCategory(updates.getCategory());
        existing.setReceiverPhone(updates.getReceiverPhone());
        // save
        Transaction saved = transactionRepository.save(existing);
        auditPublisher.publishUpdate(saved);
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        // check deletable same as editable
        validator.checkEditable(existing.getStatus());
        // set status to Deleted
        Status deletedStatus = statusRepository.findAll().stream()
                .filter(s -> "Deleted".equals(s.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Deleted status not found"));
        existing.setStatus(deletedStatus);
        transactionRepository.save(existing);
        auditPublisher.publishDelete(existing);
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> getById(UUID id) {
        return transactionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> filter(Map<String, String> params, int page, int size) {
        // TODO: implement Specification based filtering using params
        Specification<Transaction> spec = (root, query, cb) -> cb.conjunction();
        return transactionRepository.findAll(spec, PageRequest.of(page, size));
    }
}
