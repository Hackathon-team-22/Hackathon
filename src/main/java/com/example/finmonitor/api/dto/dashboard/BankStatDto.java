package com.example.finmonitor.api.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record BankStatDto(
        UUID bankId,
        String bankName,
        long count,
        BigDecimal sumAmount
) {}
