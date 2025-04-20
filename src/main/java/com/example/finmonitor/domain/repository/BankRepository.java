package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {
}