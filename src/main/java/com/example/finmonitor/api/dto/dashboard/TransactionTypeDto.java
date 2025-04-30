package com.example.finmonitor.api.dto.dashboard;

import com.example.finmonitor.application.enums.TxnType;

public record TransactionTypeDto(
        TxnType type,
        long count
) {}
