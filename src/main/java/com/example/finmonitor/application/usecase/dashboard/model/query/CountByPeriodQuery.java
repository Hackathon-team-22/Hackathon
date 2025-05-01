package com.example.finmonitor.application.usecase.dashboard.model.query;

import com.example.finmonitor.application.enums.Period;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос: динамика по количеству транзакций за период.
 */
public record CountByPeriodQuery(
        UUID userId,
        Period period,
        LocalDateTime start,
        LocalDateTime end
) { }
