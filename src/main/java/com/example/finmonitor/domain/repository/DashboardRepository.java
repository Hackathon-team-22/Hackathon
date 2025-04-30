package com.example.finmonitor.domain.repository;

import com.example.finmonitor.api.dto.dashboard.CountByPeriodDto;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для получения статистических данных по транзакциям текущего пользователя.
 */
public interface DashboardRepository {

    /**
     * Динамика по количеству транзакций за период для пользователя.
     */
    List<Object[]> countByPeriodForUser(UUID userId, String period, LocalDateTime from, LocalDateTime to);

    /**
     * Распределение транзакций по типу (Credit/Debit) за период для пользователя.
     */
    List<Object[]> countByTypeForUser(UUID userId, LocalDateTime from, LocalDateTime to);

    /**
     * Суммы поступлений и расходов за период для пользователя.
     */
    Object[] sumIncomeAndExpenseForUser(UUID userId, LocalDateTime from, LocalDateTime to);

    /**
     * Распределение транзакций по статусам за период для пользователя.
     */
    List<Object[]> countByStatusForUser(UUID userId, LocalDateTime from, LocalDateTime to);

    /**
     * Статистика по банкам-отправителям за период для пользователя.
     */
    List<Object[]> statsBySenderBankForUser(UUID userId, LocalDateTime from, LocalDateTime to);

    /**
     * Статистика по банкам-получателям за период для пользователя.
     */
    List<Object[]> statsByReceiverBankForUser(UUID userId, LocalDateTime from, LocalDateTime to);

    /**
     * Статистика по категориям расходов/доходов за период для пользователя.
     */
    List<Object[]> statsByCategoryForUser(UUID userId, LocalDateTime from, LocalDateTime to);

    /**
     * Динамика числа транзакций по периодам для заданного типа (DEBIT/CREDIT).
     */
    List<CountByPeriodDto> countByTypeAndPeriodForUser(
            UUID userId,
            TxnType typeName,
            Period period,
            LocalDateTime from,
            LocalDateTime to
    );
}
