package com.example.finmonitor.domain.repository.impl;

import com.example.finmonitor.api.dto.dashboard.CountByPeriodDto;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.domain.repository.DashboardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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

    @Override
    public List<Object[]> countByPeriodForUser(UUID userId, String period, LocalDateTime from, LocalDateTime to) {
        String sql = """
                    SELECT date_trunc(:period, t.timestamp) AS period_start,
                           COUNT(t.*)
                    FROM transactions t
                    WHERE t.created_by_user_id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                    GROUP BY period_start
                    ORDER BY period_start
                """;
        return em.createNativeQuery(sql, Object[].class)
                .setParameter("period", period)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> countByTypeForUser(UUID userId, LocalDateTime from, LocalDateTime to) {
        String jpql = """
                    SELECT tt.name, COUNT(t)
                    FROM Transaction t
                    JOIN t.transactionType tt
                    WHERE t.createdByUser.id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                    GROUP BY tt.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public Object[] sumIncomeAndExpenseForUser(UUID userId, LocalDateTime from, LocalDateTime to) {
        String jpql = """
                    SELECT
                      SUM(CASE WHEN tt.name = 'Credit' THEN t.amount ELSE 0 END),
                      SUM(CASE WHEN tt.name = 'Debit'  THEN t.amount ELSE 0 END)
                    FROM Transaction t
                    JOIN t.transactionType tt
                    WHERE t.createdByUser.id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
    }

    @Override
    public List<Object[]> countByStatusForUser(UUID userId, LocalDateTime from, LocalDateTime to) {
        String jpql = """
                    SELECT s.name, COUNT(t)
                    FROM Transaction t
                    JOIN t.status s
                    WHERE t.createdByUser.id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                    GROUP BY s.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> statsBySenderBankForUser(UUID userId, LocalDateTime from, LocalDateTime to) {
        String jpql = """
                    SELECT b.id, b.name, COUNT(t), SUM(t.amount)
                    FROM Transaction t
                    JOIN t.bankSender b
                    WHERE t.createdByUser.id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                    GROUP BY b.id, b.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> statsByReceiverBankForUser(UUID userId, LocalDateTime from, LocalDateTime to) {
        String jpql = """
                    SELECT b.id, b.name, COUNT(t), SUM(t.amount)
                    FROM Transaction t
                    JOIN t.bankReceiver b
                    WHERE t.createdByUser.id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                    GROUP BY b.id, b.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> statsByCategoryForUser(UUID userId, LocalDateTime from, LocalDateTime to) {
        String jpql = """
                    SELECT c.id, c.name, COUNT(t), SUM(t.amount)
                    FROM Transaction t
                    JOIN t.category c
                    WHERE t.createdByUser.id = :userId
                      AND t.timestamp BETWEEN :from AND :to
                    GROUP BY c.id, c.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

//    @Override
//    public List countByTypeAndPeriodForUser(UUID userId, TxnType typeName, Period period, LocalDateTime from, LocalDateTime to) {
//        String sql = """
//                    SELECT date_trunc(:period, t.timestamp) AS period_start,
//                           COUNT(t.*)
//                      FROM transactions t
//                     JOIN transaction_type tt ON t.transaction_type_id = tt.id
//                     WHERE t.created_by_user_id = :userId
//                       AND tt.name = :typeName
//                       AND t.timestamp BETWEEN :from AND :to
//                     GROUP BY period_start
//                     ORDER BY period_start
//                """;
//        return em.createNativeQuery(sql, Object[].class)
//                .setParameter("period", period)
//                .setParameter("userId", userId)
//                .setParameter("typeName", typeName)
//                .setParameter("from", from)
//                .setParameter("to", to)
//                .getResultList();
//    }
    @Override
    public List<CountByPeriodDto> countByTypeAndPeriodForUser(
            UUID userId,
            TxnType type,
            Period period,
            LocalDateTime start,
            LocalDateTime end) {

        // 1. Выбираем соответствующую строку для date_trunc
        String truncUnit;
        switch (period) {
            case DAY:     truncUnit = "day";     break;
            case WEEK:    truncUnit = "week";    break;
            case MONTH:   truncUnit = "month";   break;
            case QUARTER: truncUnit = "quarter"; break;
            case YEAR:    truncUnit = "year";    break;
            default: throw new IllegalArgumentException("Unsupported period: " + period);
        }

        // 2. Строим SQL с литералом
        String sql = String.format("""
                SELECT date_trunc('%s', t.timestamp) AS period_start,
                       COUNT(*) AS cnt
                  FROM transactions t
                 JOIN transaction_type tt ON t.transaction_type_id = tt.id
                 WHERE t.created_by_user_id = :userId
                   AND tt.name = :typeName
                   AND t.timestamp BETWEEN :start AND :end
                 GROUP BY period_start
                 ORDER BY period_start
                """, truncUnit);

        // 3. Выполняем запрос
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("typeName", type.name())
                .setParameter("start", Timestamp.valueOf(start))
                .setParameter("end",   Timestamp.valueOf(end))
                .getResultList();

        // 4. Мапим в DTO
        List<CountByPeriodDto> result = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Timestamp ts = Timestamp.from((Instant) row[0]);
            Number cnt    = (Number)    row[1];
            // Представляем период как строку, например "2025-01-01T00:00:00"
            LocalDate periodLabel = ts.toLocalDateTime().toLocalDate();
            result.add(new CountByPeriodDto(periodLabel, cnt.longValue()));
        }

        return result;
    }


}
