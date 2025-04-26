package com.example.finmonitor.application.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record BankStatDto(
        UUID bankId,
        String bankName,
        long count,
        BigDecimal sumAmount
) {}
