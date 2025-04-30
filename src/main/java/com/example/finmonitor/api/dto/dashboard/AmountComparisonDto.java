package com.example.finmonitor.api.dto.dashboard;

import java.math.BigDecimal;

public record AmountComparisonDto(
        BigDecimal totalCredit,
        BigDecimal totalDebit
) {}
