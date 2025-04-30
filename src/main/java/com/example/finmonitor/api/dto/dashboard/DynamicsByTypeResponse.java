package com.example.finmonitor.api.dto.dashboard;

/**
 * 2b. Ответ: динамика транзакций по типу (тот же вид, что и CountByPeriodResponse)
 */
public record DynamicsByTypeResponse(
        java.time.LocalDate period_start,
        long count
) {
}
