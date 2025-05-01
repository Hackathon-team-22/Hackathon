package com.example.finmonitor.application.usecase.dashboard.model.query;

import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос: распределение по типу транзакции за период.
 */
public record CountByTypeQuery(
        UUID userId,
        TxnType type,
        Period period,
        LocalDateTime start,
        LocalDateTime end
) { }
