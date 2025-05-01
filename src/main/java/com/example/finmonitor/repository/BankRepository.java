package com.example.finmonitor.repository;

import com.example.finmonitor.domain.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {
    Optional<Bank> findByName(String name);


    default UUID getOrCreateByName(String name) {
        return findByName(name)
                .map(Bank::getId)
                .orElseGet(() -> save(new Bank(name)).getId());
    }
}