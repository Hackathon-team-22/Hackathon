package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Результат: статистика по банкам-отправителям или банкам-получателям.
 */
public record StatsByBankResult(
        UUID bankId,
        String bankName,
        LocalDate period_start,
        long transactionCount,
        BigDecimal totalAmount
) {
}
