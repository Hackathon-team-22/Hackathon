package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Результат: сравнение сумм поступлений и расходов.
 */
public record CompareFundsResult(
        LocalDate period_start,
        BigDecimal totalCredit,
        BigDecimal totalDebit
) { }
