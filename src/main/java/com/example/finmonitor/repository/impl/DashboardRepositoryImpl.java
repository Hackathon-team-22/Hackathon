package com.example.finmonitor.repository.impl;

import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.application.usecase.dashboard.model.result.*;
import com.example.finmonitor.repository.DashboardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager em;

    private String truncUnit(Period period) {
        return switch (period) {
            case WEEK -> "week";
            case MONTH -> "month";
            case QUARTER -> "quarter";
            case YEAR -> "year";
        };
    }

    @Override
    public List<CountByPeriodResult> fetchCountByPeriod(UUID userId, Period period, LocalDateTime start, LocalDateTime end) {
        String unit = truncUnit(period);
        String sqlTemplate = """
                SELECT date_trunc('%s', t.timestamp) AS period_start,
                       COUNT(*)       AS cnt
                  FROM transactions t
                 WHERE t.created_by_user_id = :userId
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start
                 ORDER BY period_start
                """;
        String sql = String.format(sqlTemplate, unit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end", Timestamp.valueOf(end))
                .getResultList();

        List<CountByPeriodResult> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Timestamp ts = Timestamp.from((Instant) row[0]);
            LocalDate periodStart = ts.toLocalDateTime().toLocalDate();
            long cnt = ((Number) row[1]).longValue();
            result.add(new CountByPeriodResult(periodStart, cnt));
        }
        return result;
    }

    @Override
    public List<DynamicsByTypeResult> fetchDynamicsByType(UUID userId, TxnType type, Period period, LocalDateTime start, LocalDateTime end) {
        String unit = truncUnit(period);
        String sqlTemplate = """
                SELECT date_trunc('%s', t.timestamp) AS period_start, COUNT(*) AS cnt
                  FROM transactions t
                  JOIN transaction_type tt ON t.transaction_type_id = tt.id
                 WHERE t.created_by_user_id = :userId
                   AND tt.name = :typeName
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start, tt.name
                 ORDER BY period_start
                """;
        String sql = String.format(sqlTemplate, unit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("typeName", type.name())
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end", Timestamp.valueOf(end))
                .getResultList();
        List<DynamicsByTypeResult> result = new ArrayList<>(rows.size());

        for (Object[] row : rows) {
            Timestamp ts = Timestamp.from((Instant) row[0]);
            LocalDate periodStart = ts.toLocalDateTime().toLocalDate();
            long cnt = ((Number) row[1]).longValue();
            result.add(new DynamicsByTypeResult(periodStart, cnt));
        }
        return result;
    }

    @Override
    public List<CompareFundsResult> fetchCompareFunds(UUID userId, Period period, LocalDateTime start, LocalDateTime end) {
        String unit = truncUnit(period);
        String sqlTemplate = """
                SELECT date_trunc('%s', t.timestamp) AS period_start,
                  SUM(CASE WHEN tt.name = 'CREDIT' THEN t.amount ELSE 0 END) AS total_credit,
                  SUM(CASE WHEN tt.name = 'DEBIT'  THEN t.amount ELSE 0 END) AS total_debit
                  FROM transactions t
                  JOIN transaction_type tt ON t.transaction_type_id = tt.id
                 WHERE t.created_by_user_id = :userId
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start
                 ORDER BY period_start
                """;
        String sql = String.format(sqlTemplate, unit);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end", Timestamp.valueOf(end))
                .getResultList();

        List<CompareFundsResult> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            BigDecimal credit = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            BigDecimal debit = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            Timestamp ts = Timestamp.from((Instant) row[0]);
            LocalDate periodStart = ts.toLocalDateTime().toLocalDate();
            result.add(new CompareFundsResult(periodStart, credit, debit));
        }
        return result;
    }

    @Override
    public List<CountByStatusResult> fetchCountByStatus(
            UUID userId,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String unit = truncUnit(period);
        String sqlTemplate = """
                SELECT date_trunc('%s', t.timestamp) AS period_start,
                   SUM(CASE WHEN lower(s.name) = 'completed' THEN 1 ELSE 0 END)   AS completed,
                   SUM(CASE WHEN lower(s.name) = 'cancelled' THEN 1 ELSE 0 END)   AS cancelled
                  FROM transactions t
                  JOIN status s ON t.status_id = s.id
                 WHERE t.created_by_user_id = :userId
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start
                 ORDER BY period_start
                """;
        String sql = String.format(sqlTemplate, unit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end", Timestamp.valueOf(end))
                .getResultList();

        List<CountByStatusResult> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Timestamp ts = Timestamp.from((Instant) row[0]);
            LocalDate periodStart = ts.toLocalDateTime().toLocalDate();
            long completed = ((Number) row[1]).longValue();
            long cancelled = ((Number) row[2]).longValue();
            result.add(new CountByStatusResult(
                    periodStart,
                    completed,
                    cancelled
            ));
        }
        return result;
    }

    @Override
    public List<StatsByBankResult> fetchStatsByBank(
            UUID userId,
            DashboardRole role,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String unit = truncUnit(period);
        String joinClause = role == DashboardRole.SENDER
                ? "JOIN bank b ON t.bank_sender_id = b.id"
                : "JOIN bank b ON t.bank_receiver_id = b.id";
        String sqlTemplate = """
                SELECT date_trunc('%s', t.timestamp) AS period_start,
                       b.id                                AS bank_id,
                       b.name                              AS bank_name,
                       COUNT(*)                            AS cnt,
                       SUM(t.amount)                       AS total
                  FROM transactions t
                  %s
                 WHERE t.created_by_user_id = :userId
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start, b.id, b.name
                 ORDER BY period_start
                """;
        String sql = String.format(sqlTemplate, unit, joinClause);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end", Timestamp.valueOf(end))
                .getResultList();

        List<StatsByBankResult> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Timestamp ts = Timestamp.from((Instant) row[0]);
            LocalDate periodStart = ts.toLocalDateTime().toLocalDate();
            UUID bankId = row[1] instanceof UUID
                    ? (UUID) row[1]
                    : UUID.fromString(row[1].toString());
            String bankName = (String) row[2];
            long cnt = ((Number) row[3]).longValue();
            BigDecimal total = row[4] instanceof BigDecimal
                    ? (BigDecimal) row[4]
                    : new BigDecimal(row[4].toString());

            result.add(new StatsByBankResult(
                    bankId,
                    bankName,
                    periodStart,
                    cnt,
                    total
            ));
        }
        return result;
    }

    @Override
    public List<StatsByCategoryResult> fetchStatsByCategory(
            UUID userId,
            TxnType type,
            Period period,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String unit = truncUnit(period);
        String sqlTemplate = """
                SELECT date_trunc('%s', t.timestamp) AS period_start,
                       c.id                                AS category_id,
                       c.name                              AS category_name,
                       COUNT(*)                            AS cnt,
                       SUM(t.amount)                       AS total
                  FROM transactions t
                  JOIN category c ON t.category_id = c.id
                  JOIN transaction_type tt ON t.transaction_type_id = tt.id
                 WHERE t.created_by_user_id = :userId
                   AND tt.name = :typeName
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start, c.id, c.name
                 ORDER BY period_start
                """;
        String sql = String.format(sqlTemplate, unit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("typeName", type.name())
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end", Timestamp.valueOf(end))
                .getResultList();

        List<StatsByCategoryResult> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Timestamp ts = Timestamp.from((Instant) row[0]);
            LocalDate periodStart = ts.toLocalDateTime().toLocalDate();
            UUID categoryId = row[1] instanceof UUID
                    ? (UUID) row[1]
                    : UUID.fromString(row[1].toString());
            String categoryName = (String) row[2];
            long cnt = ((Number) row[3]).longValue();
            BigDecimal total = row[4] instanceof BigDecimal
                    ? (BigDecimal) row[4]
                    : new BigDecimal(row[4].toString());
            result.add(new StatsByCategoryResult(
                    categoryId,
                    categoryName,
                    periodStart,
                    cnt,
                    total,
                    type
            ));
        }
        return result;
    }
}
