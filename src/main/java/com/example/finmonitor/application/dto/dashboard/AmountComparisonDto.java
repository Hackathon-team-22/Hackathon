package com.example.finmonitor.application.dto.dashboard;

import java.math.BigDecimal;

public record AmountComparisonDto(
        BigDecimal totalCredit,
        BigDecimal totalDebit
) {}
