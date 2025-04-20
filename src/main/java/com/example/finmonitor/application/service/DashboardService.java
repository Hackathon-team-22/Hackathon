package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private TransactionRepository transactionRepository;

    // 1. Динамика по количеству транзакций
    public List<Map<String, Object>> countByPeriod(String period) {
        // period = week|month|quarter|year
        // TODO: написать SQL агрегирующий запрос через JdbcTemplate или @Query
        return List.of();
    }

    // 2. Динамика по типу транзакции
    public Map<String, List<Map<String, Object>>> byType(String period) {
        // возвращает {credit: [...], debit: [...]} временные ряды
        return Map.of("credit", List.of(), "debit", List.of());
    }

    // 3. Сравнение сумм
    public Map<String, Object> sumComparison(String period) {
        // {incoming: sum, outgoing: sum}
        return Map.of("incoming", 0, "outgoing", 0);
    }

    // 4. Проведённые vs отменённые
    public Map<String, Long> statusCount(String period) {
        // {completed: N, cancelled: M}
        return Map.of("completed", 0L, "cancelled", 0L);
    }

    // 5. Статистика по банкам
    public List<Map<String, Object>> byBank(String role, String period) {
        // role = sender|receiver, возвращает [{bankId,name,count,sum}, ...]
        return List.of();
    }

    // 6. Статистика по категориям
    public List<Map<String, Object>> byCategory(String type, String period) {
        // type = expense|income
        return List.of();
    }
}
