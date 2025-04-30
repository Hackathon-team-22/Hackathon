package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.finmonitor.application.enums.TxnType;

/**
 * Результат: статистика по категориям расходов/доходов.
 */
public record StatsByCategoryResult(
        UUID categoryId,
        String categoryName,
        LocalDate period_start,
        long transactionCount,
        BigDecimal totalAmount,
        TxnType type
) {
}
