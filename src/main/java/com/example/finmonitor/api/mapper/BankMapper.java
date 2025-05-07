package com.example.finmonitor.api.mapper;

import com.example.finmonitor.api.dto.BankResponse;
import com.example.finmonitor.domain.model.Bank;
import org.springframework.stereotype.Component;

@Component
public class BankMapper {
    public BankResponse toResponse(Bank bank) {
        return new BankResponse(bank.getId(), bank.getName());
    }
}
