package com.example.finmonitor.application.dto.dashboard;

import com.example.finmonitor.application.enums.TxnType;

public record TransactionTypeDto(
        TxnType type,
        long count
) {}
