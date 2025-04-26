package com.example.finmonitor.domain.repository.impl;

import com.example.finmonitor.domain.repository.DashboardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Object[]> countGroupedByPeriod(String period, LocalDateTime from, LocalDateTime to) {
        // Нативный SQL
        String sql = """
                    SELECT
                      date_trunc(:period, t.timestamp) AS period_start,
                      COUNT(*)               AS cnt
                    FROM transactions t
                    WHERE t.timestamp BETWEEN :from AND :to
                    GROUP BY 1
                    ORDER BY 1
                """;

        // Выполняем запрос
        @SuppressWarnings("unchecked")
        List<Object[]> raw = em.createNativeQuery(sql)
                .setParameter("period", period)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        // Нормализуем первый столбец: Instant или LocalDateTime → java.sql.Timestamp
        List<Object[]> normalized = new ArrayList<>(raw.size());
        for (Object[] row : raw) {
            Object tsValue = row[0];
            if (tsValue instanceof java.time.Instant inst) {
                row[0] = java.sql.Timestamp.from(inst);
            } else if (tsValue instanceof java.time.LocalDateTime ldt) {
                row[0] = java.sql.Timestamp.valueOf(ldt);
            }
            // если уже java.sql.Timestamp — ничего не делаем
            normalized.add(row);
        }

        return normalized;
    }


    @Override
    public List<Object[]> countByTransactionType(LocalDateTime from, LocalDateTime to) {
        var jpql = """
                    SELECT t.transactionType.name, COUNT(t)
                    FROM Transaction t
                    WHERE t.timestamp BETWEEN :from AND :to
                    GROUP BY t.transactionType.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public Object[] sumIncomeAndExpense(LocalDateTime from, LocalDateTime to) {
        var jpql = """
                    SELECT
                      SUM(CASE WHEN tt.name = 'Credit' THEN t.amount ELSE 0 END),
                      SUM(CASE WHEN tt.name = 'Debit'  THEN t.amount ELSE 0 END)
                    FROM Transaction t
                    JOIN t.transactionType tt
                    WHERE t.timestamp BETWEEN :from AND :to
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
    }

    @Override
    public List<Object[]> countByStatus(LocalDateTime from, LocalDateTime to) {
        var jpql = """
                    SELECT t.status.name, COUNT(t)
                    FROM Transaction t
                    WHERE t.timestamp BETWEEN :from AND :to
                    GROUP BY t.status.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> statsBySenderBank(LocalDateTime from, LocalDateTime to) {
        var jpql = """
                SELECT b.id, b.name, COUNT(t), SUM(t.amount)
                FROM Transaction t
                JOIN t.bankSender b
                WHERE t.timestamp BETWEEN :from AND :to
                GROUP BY b.id, b.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> statsByReceiverBank(LocalDateTime from, LocalDateTime to) {
        var jpql = """
                SELECT b.id, b.name, COUNT(t), SUM(t.amount)
                FROM Transaction t
                JOIN t.bankReceiver b
                WHERE t.timestamp BETWEEN :from AND :to
                GROUP BY b.id, b.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<Object[]> statsByCategory(LocalDateTime from, LocalDateTime to) {
        var jpql = """
                    SELECT c.id, c.name, COUNT(t), SUM(t.amount)
                    FROM Transaction t
                    JOIN t.category c
                    WHERE t.timestamp BETWEEN :from AND :to
                    GROUP BY c.id, c.name
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}