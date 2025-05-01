package com.example.finmonitor.application.usecase.dashboard.model.result;

import java.time.LocalDate;
import com.example.finmonitor.application.enums.TxnType;

/**
 * Результат: распределение по типу транзакции за период.
 */
public record CountByTypeResult(
        LocalDate period_start,
        long count,
        TxnType type
) { }
