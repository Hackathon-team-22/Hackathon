package com.example.finmonitor.application.service.impl;

import com.example.finmonitor.application.service.BankService;
import com.example.finmonitor.domain.model.Bank;
import com.example.finmonitor.repository.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public List<Bank> getAll() {
        return bankRepository.findAll();
    }

    @Override
    public Optional<Bank> getById(UUID id) {
        return bankRepository.findById(id);
    }
}