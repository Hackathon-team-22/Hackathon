package com.example.finmonitor.application.service;

import com.example.finmonitor.application.dto.dashboard.*;
import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.domain.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    // 1. Динамика по количеству транзакций
    public List<CountByPeriodDto> countByPeriod(UUID userId, Period period) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = calculateFrom(period, to);

        return dashboardRepository
                .countByPeriodForUser(userId, period.name().toLowerCase(), from, to)
                .stream()
                .map(arr -> {
                    // arr[0] — java.sql.Timestamp, а не LocalDateTime
                    Instant instant = (Instant) arr[0];
                    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    long count = ((Number) arr[1]).longValue();
                    return new CountByPeriodDto(date, count);
                })
                .collect(Collectors.toList());
    }

    // 2. Динамика по типу транзакции
    public List<TransactionTypeDto> byTransactionType(UUID userId, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        return dashboardRepository.countByTypeForUser(userId, from, to)
                .stream()
                .map(arr -> new TransactionTypeDto(
                        TxnType.valueOf(((String) arr[0]).toUpperCase()),
                        ((Number) arr[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // 3. Сравнение сумм поступлений и расходов
    public AmountComparisonDto compareAmounts(UUID userId, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        Object[] sums = dashboardRepository.sumIncomeAndExpenseForUser(userId, from, to);
        BigDecimal income = (BigDecimal) sums[0];
        BigDecimal expense = (BigDecimal) sums[1];
        return new AmountComparisonDto(income, expense);
    }

    // 4. Количество по статусам
    public List<ExecutionStatusDto> byStatus(UUID userId, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        return dashboardRepository.countByStatusForUser(userId, from, to)
                .stream()
                .map(arr -> new ExecutionStatusDto(
                        (String) arr[0],
                        ((Number) arr[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // 5. Статистика по банкам (sender или receiver)
    public List<BankStatDto> byBank(UUID userId, DashboardRole role, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        List<Object[]> raw = role == DashboardRole.SENDER
                ? dashboardRepository.statsBySenderBankForUser(userId, from, to)
                : dashboardRepository.statsByReceiverBankForUser(userId, from, to);
        return raw.stream()
                .map(arr -> new BankStatDto(
                        (java.util.UUID) arr[0],
                        (String) arr[1],
                        ((Number) arr[2]).longValue(),
                        (BigDecimal) arr[3]
                ))
                .collect(Collectors.toList());
    }

    // 6. Статистика по категориям
    public List<CategoryStatDto> byCategory(UUID userId, TxnType type, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        // Если нужно фильтровать по типу – можно дофильтровать здесь,
        // или расширить репозиторий, но для простоты:
        return dashboardRepository.statsByCategoryForUser(userId, from, to)
                .stream()
                .map(arr -> new CategoryStatDto(
                        (java.util.UUID) arr[0],
                        (String) arr[1],
                        ((Number) arr[2]).longValue(),
                        (BigDecimal) arr[3]
                ))
                .collect(Collectors.toList());
    }

    // Вспомогательный метод: вычисляет дату начала периода
    private LocalDateTime calculateFrom(Period period, LocalDateTime to) {
        return switch (period) {
            case WEEK -> to.minusWeeks(1);
            case MONTH -> to.minusMonths(1);
            case QUARTER -> to.minusMonths(3);
            case YEAR -> to.minusYears(1);
        };
    }
}
