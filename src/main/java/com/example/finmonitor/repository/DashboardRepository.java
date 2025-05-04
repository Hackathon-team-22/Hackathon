package com.example.finmonitor.repository;

import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.application.usecase.dashboard.model.result.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для агрегирования статистики дашборда.
 */
public interface DashboardRepository {

    /**
     * Динамика по количеству транзакций за период.
     */
    List<CountByPeriodResult> fetchCountByPeriod(
            UUID userId,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Динамика транзакций конкретного типа за период.
     */
    List<DynamicsByTypeResult> fetchDynamicsByType(
            UUID userId,
            TxnType type,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Сравнение сумм поступлений и расходов за период.
     */
    List<CompareFundsResult> fetchCompareFunds(
            UUID userId,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Количество проведённых и отменённых транзакций за период.
     */
    List<CountByStatusResult> fetchCountByStatus(
            UUID userId,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Статистика по банкам-отправителям или банкам-получателям за период.
     */
    List<StatsByBankResult> fetchStatsByBank(
            UUID userId,
            DashboardRole role,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Статистика по категориям расходов/доходов за период.
     */
    List<StatsByCategoryResult> fetchStatsByCategory(
            UUID userId,
            TxnType type,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    );
}
