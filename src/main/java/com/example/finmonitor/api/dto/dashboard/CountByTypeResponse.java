package com.example.finmonitor.api.dto.dashboard;

import java.time.LocalDate;

import com.example.finmonitor.application.enums.TxnType;

/**
 * 2. Ответ: распределение по типу транзакции
 */
public record CountByTypeResponse(
        LocalDate period_start,
        long count,
        TxnType type
) {
}
