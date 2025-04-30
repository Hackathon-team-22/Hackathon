package com.example.finmonitor.application.usecase.dashboard.model.query;

import com.example.finmonitor.application.enums.Period;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос: количество проведённых и отменённых транзакций за период.
 */
public record CountByStatusQuery(
        UUID userId,
        Period period,
        LocalDateTime start,
        LocalDateTime end
) { }
