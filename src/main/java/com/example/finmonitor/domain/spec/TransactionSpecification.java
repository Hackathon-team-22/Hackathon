package com.example.finmonitor.domain.spec;

import com.example.finmonitor.domain.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> withFilters(
            UUID bankSenderId,
            UUID bankReceiverId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            UUID statusId,
            String receiverTin,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            UUID transactionTypeId,
            UUID categoryId
    ) {
        return (root, query, cb) -> {
            var p = cb.conjunction();

            if (bankSenderId != null) {
                p = cb.and(p, cb.equal(root.join("bankSender", JoinType.LEFT).get("id"), bankSenderId));
            }
            if (bankReceiverId != null) {
                p = cb.and(p, cb.equal(root.join("bankReceiver", JoinType.LEFT).get("id"), bankReceiverId));
            }
            if (fromDate != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("timestamp"), fromDate));
            }
            if (toDate != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("timestamp"), toDate));
            }
            if (statusId != null) {
                p = cb.and(p, cb.equal(root.join("status", JoinType.LEFT).get("id"), statusId));
            }
            if (receiverTin != null) {
                p = cb.and(p, cb.equal(root.get("receiverTin"), receiverTin));
            }
            if (minAmount != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            if (transactionTypeId != null) {
                p = cb.and(p, cb.equal(root.join("transactionType", JoinType.LEFT).get("id"), transactionTypeId));
            }
            if (categoryId != null) {
                p = cb.and(p, cb.equal(root.join("category", JoinType.LEFT).get("id"), categoryId));
            }

            return p;
        };
    }
}
