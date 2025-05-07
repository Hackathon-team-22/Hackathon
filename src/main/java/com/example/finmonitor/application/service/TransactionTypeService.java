package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionTypeService {
    List<TransactionType> getAll();
    Optional<TransactionType> getById(UUID id);
}