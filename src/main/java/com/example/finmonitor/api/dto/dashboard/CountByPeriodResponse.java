package com.example.finmonitor.api.dto.dashboard;

import java.time.LocalDate;

/**
 * 1. Ответ: динамика по количеству транзакций
 */
public record CountByPeriodResponse(
        LocalDate period_start,
        long count
) {
}
