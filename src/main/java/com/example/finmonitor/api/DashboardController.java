package com.example.finmonitor.api;

import com.example.finmonitor.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Динамика по количеству транзакций в разрезе периодов (week/month/quarter/year)
     */
    @GetMapping("/transactions/count")
    public List<Map<String, Object>> countByPeriod(
            @Parameter(
                    description = "Период выборки данных: week (последняя неделя), month (последний месяц), quarter (последний квартал), year (последний год). По умолчанию week.",
                    example = "week"
            )
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.countByPeriod(period);
    }

    /**
     * Динамика по типу транзакции (debit/credit)
     */
    @GetMapping("/transactions/by-type")
    public Map<String, List<Map<String, Object>>> byType(
            @Parameter(
                    description = "Период выборки данных: week, month, quarter, year. По умолчанию week.",
                    example = "week"
            )
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.byType(period);
    }

    /**
     * Сравнение суммы поступлений и списаний
     */
    @GetMapping("/transactions/sums")
    public Map<String, Object> sumComparison(
            @Parameter(
                    description = "Период выборки данных: week, month, quarter, year. По умолчанию week.",
                    example = "week"
            )
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.sumComparison(period);
    }

    /**
     * Количество проведённых и отменённых транзакций
     */
    @GetMapping("/transactions/status-count")
    public Map<String, Long> statusCount(
            @Parameter(
                    description = "Период выборки данных: week, month, quarter, year. По умолчанию week.",
                    example = "week"
            )
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.statusCount(period);
    }

    /**
     * Статистика по банкам отправителя/получателя
     */
    @GetMapping("/transactions/by-bank")
    public List<Map<String, Object>> byBank(
            @Parameter(
                    description = "Роль: sender (отправитель) или receiver (получатель).",
                    example = "sender"
            )
            @RequestParam(name = "role") String role,
            @Parameter(
                    description = "Период выборки данных: week, month, quarter, year. По умолчанию week.",
                    example = "week"
            )
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.byBank(role, period);
    }

    /**
     * Статистика по категориям расходов и поступлений
     */
    @GetMapping("/transactions/by-category")
    public List<Map<String, Object>> byCategory(
            @Parameter(
                    description = "Тип транзакции: debit или credit.",
                    example = "debit"
            )
            @RequestParam(name = "type") String type,
            @Parameter(
                    description = "Период выборки данных: week, month, quarter, year. По умолчанию week.",
                    example = "week"
            )
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.byCategory(type, period);
    }
}
