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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    // 1. Динамика по количеству транзакций
    public List<CountByPeriodDto> countByPeriod(Period period) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = calculateFrom(period, to);

        return dashboardRepository
                .countGroupedByPeriod(period.name().toLowerCase(), from, to)
                .stream()
                .map(arr -> {
                    // arr[0] — java.sql.Timestamp, а не LocalDateTime
                    Timestamp ts = (Timestamp) arr[0];
                    LocalDate date = ts.toLocalDateTime().toLocalDate();
                    long count = ((Number) arr[1]).longValue();
                    return new CountByPeriodDto(date, count);
                })
                .collect(Collectors.toList());
    }

    // 2. Динамика по типу транзакции
    public List<TransactionTypeDto> byTransactionType(Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        return dashboardRepository.countByTransactionType(from, to)
                .stream()
                .map(arr -> new TransactionTypeDto(
                        TxnType.valueOf(((String) arr[0]).toUpperCase()),
                        ((Number) arr[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // 3. Сравнение сумм поступлений и расходов
    public AmountComparisonDto compareAmounts(Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        Object[] sums = dashboardRepository.sumIncomeAndExpense(from, to);
        BigDecimal income = (BigDecimal) sums[0];
        BigDecimal expense = (BigDecimal) sums[1];
        return new AmountComparisonDto(income, expense);
    }

    // 4. Количество по статусам
    public List<ExecutionStatusDto> byStatus(Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        return dashboardRepository.countByStatus(from, to)
                .stream()
                .map(arr -> new ExecutionStatusDto(
                        (String) arr[0],
                        ((Number) arr[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // 5. Статистика по банкам (sender или receiver)
    public List<BankStatDto> byBank(DashboardRole role, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        List<Object[]> raw = role == DashboardRole.SENDER
                ? dashboardRepository.statsBySenderBank(from, to)
                : dashboardRepository.statsByReceiverBank(from, to);
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
    public List<CategoryStatDto> byCategory(TxnType type, Period period) {
        var to = LocalDateTime.now();
        var from = calculateFrom(period, to);
        // Если нужно фильтровать по типу – можно дофильтровать здесь,
        // или расширить репозиторий, но для простоты:
        return dashboardRepository.statsByCategory(from, to)
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
