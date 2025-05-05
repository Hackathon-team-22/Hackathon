package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.Bank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankService {
    List<Bank> getAll();
    Optional<Bank> getById(UUID id);
}