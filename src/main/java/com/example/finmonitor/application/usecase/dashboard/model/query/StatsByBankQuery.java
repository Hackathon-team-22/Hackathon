package com.example.finmonitor.application.usecase.dashboard.model.query;

import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос: статистика по банкам-отправителям или банкам-получателям за период.
 */
public record StatsByBankQuery(
        UUID userId,
        DashboardRole role,
        Period period,
        LocalDateTime start,
        LocalDateTime end
) { }
