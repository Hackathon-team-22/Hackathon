package com.example.finmonitor.application.service.impl;

import com.example.finmonitor.application.service.TransactionTypeService;
import com.example.finmonitor.domain.model.TransactionType;
import com.example.finmonitor.repository.TransactionTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionTypeServiceImpl implements TransactionTypeService {

    private final TransactionTypeRepository trabsactionTypeRepository;

    public TransactionTypeServiceImpl(TransactionTypeRepository trabsactionTypeRepository) {
        this.trabsactionTypeRepository = trabsactionTypeRepository;
    }

    @Override
    public List<TransactionType> getAll() {
        return trabsactionTypeRepository.findAll();
    }

    @Override
    public Optional<TransactionType> getById(UUID id) {
        return trabsactionTypeRepository.findById(id);
    }
}