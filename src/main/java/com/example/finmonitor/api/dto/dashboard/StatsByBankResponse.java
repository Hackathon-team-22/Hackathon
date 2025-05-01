package com.example.finmonitor.api.dto.dashboard;

import java.time.LocalDate;
import java.util.UUID;
import java.math.BigDecimal;

/**
 * 5. Ответ: статистика по банкам отправителя/получателя
 */
public record StatsByBankResponse(
        UUID bankId,
        String bankName,
        LocalDate period_start,
        long transactionCount,
        BigDecimal totalAmount
) {
}
