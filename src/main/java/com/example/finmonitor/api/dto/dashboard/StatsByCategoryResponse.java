package com.example.finmonitor.api.dto.dashboard;

import java.time.LocalDate;
import java.util.UUID;
import java.math.BigDecimal;

import com.example.finmonitor.application.enums.TxnType;

/**
 * 6. Ответ: статистика по категориям расходов/доходов
 */
public record StatsByCategoryResponse(
        UUID categoryId,
        String categoryName,
        LocalDate period_start,
        long transactionCount,
        BigDecimal totalAmount,
        TxnType type
) {
}
