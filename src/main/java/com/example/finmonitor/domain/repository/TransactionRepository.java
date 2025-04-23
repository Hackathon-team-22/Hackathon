// src/main/java/com/example/finmonitor/domain/repository/TransactionRepository.java
package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE (:bankSenderId IS NULL OR t.bankSender.id = :bankSenderId)
          AND (:bankReceiverId IS NULL OR t.bankReceiver.id = :bankReceiverId)
          AND (:fromDate IS NULL OR t.timestamp >= :fromDate)
          AND (:toDate IS NULL OR t.timestamp <= :toDate)
          AND (:statusId IS NULL OR t.status.id = :statusId)
          AND (:receiverTin IS NULL OR t.receiverTin = :receiverTin)
          AND (:minAmount IS NULL OR t.amount >= :minAmount)
          AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
          AND (:transactionTypeId IS NULL OR t.transactionType.id = :transactionTypeId)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
    """)

    long countByTimestampBetween(LocalDate fromDate, LocalDate toDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status.name = :statusName AND t.timestamp BETWEEN :fromDate AND :toDate")
    long countByStatusNameAndTimestampBetween(@Param("statusName") String statusName, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
