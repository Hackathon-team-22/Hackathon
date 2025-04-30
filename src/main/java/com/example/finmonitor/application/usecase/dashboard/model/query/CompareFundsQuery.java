package com.example.finmonitor.application.usecase.dashboard.model.query;

import com.example.finmonitor.application.enums.Period;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос: сравнение сумм поступлений и расходов за период.
 */
public record CompareFundsQuery(
        UUID userId,
        Period period,
        LocalDateTime start,
        LocalDateTime end
) { }
