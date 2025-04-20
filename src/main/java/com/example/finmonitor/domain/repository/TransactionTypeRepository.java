package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, UUID> {
}