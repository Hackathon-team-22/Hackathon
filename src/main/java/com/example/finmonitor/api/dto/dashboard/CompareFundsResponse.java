package com.example.finmonitor.api.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 3. Ответ: сравнение сумм поступлений и расходов
 */
public record CompareFundsResponse(
        LocalDate period_start,
        BigDecimal totalCredit,
        BigDecimal totalDebit
) {
}
