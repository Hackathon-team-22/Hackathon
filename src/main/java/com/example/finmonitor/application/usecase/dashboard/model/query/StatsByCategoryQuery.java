package com.example.finmonitor.application.usecase.dashboard.model.query;

import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос: статистика по категориям расходов/доходов за период.
 */
public record StatsByCategoryQuery(
        UUID userId,
        TxnType type,
        Period period,
        LocalDateTime start,
        LocalDateTime end
) { }
