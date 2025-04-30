package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.time.LocalDate;

/**
 * Результат: динамика по количеству транзакций за период.
 */
public record CountByPeriodResult(
        LocalDate period_start,
        long count
) { }
