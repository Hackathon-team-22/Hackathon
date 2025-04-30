package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.time.LocalDate;

/**
 * Результат: количество проведённых и отменённых транзакций за период.
 */
public record CountByStatusResult(
        LocalDate period_start,
        long completedCount,
        long cancelledCount
) { }
