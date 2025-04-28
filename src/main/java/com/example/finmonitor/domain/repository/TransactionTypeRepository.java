package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.PartyType;
import com.example.finmonitor.domain.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, UUID> {
    Optional<TransactionType> findByName(String name);

    default UUID getOrCreateByName(String name) {
        return findByName(name)
                .map(TransactionType::getId)
                .orElseGet(() -> save(new TransactionType(name)).getId());
    }

}