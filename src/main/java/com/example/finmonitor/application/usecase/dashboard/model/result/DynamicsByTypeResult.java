package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.time.LocalDate;

/**
 * Результат: динамика (временной ряд) транзакций конкретного типа.
 */
public record DynamicsByTypeResult(
        LocalDate period_start,
        long count
) { }
