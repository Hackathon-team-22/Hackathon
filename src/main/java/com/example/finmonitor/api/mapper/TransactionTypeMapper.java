package com.example.finmonitor.api.mapper;

import com.example.finmonitor.api.dto.TransactionTypeResponse;
import com.example.finmonitor.domain.model.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeMapper {
    public TransactionTypeResponse toResponse(TransactionType transactionType) {
        return new TransactionTypeResponse(transactionType.getId(), transactionType.getName());
    }
}
