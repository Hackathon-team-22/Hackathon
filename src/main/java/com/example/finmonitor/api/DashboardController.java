package com.example.finmonitor.api;

import com.example.finmonitor.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Аналитика и статистика транзакций")
@SecurityRequirement(name = "BearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/transactions/count")
    @Operation(summary = "Динамика количества транзакций по периодам",
            responses = @ApiResponse(responseCode = "200", description = "Список агрегированных значений",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    )
    public List<Map<String, Object>> countByPeriod(
            @Parameter(description = "Период: week, month, quarter, year", example = "week")
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.countByPeriod(period);
    }

    @GetMapping("/transactions/by-type")
    @Operation(summary = "Анализ по типу транзакции (debit/credit)",
            responses = @ApiResponse(responseCode = "200", description = "Группировка по типу",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    )
    public Map<String, List<Map<String, Object>>> byType(
            @Parameter(description = "Период", example = "week")
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.byType(period);
    }

    @GetMapping("/transactions/sums")
    @Operation(summary = "Сравнение сумм поступлений и списаний",
            responses = @ApiResponse(responseCode = "200", description = "Суммы по категориям",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    )
    public Map<String, Object> sumComparison(
            @Parameter(description = "Период", example = "month")
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.sumComparison(period);
    }

    @GetMapping("/transactions/status-count")
    @Operation(summary = "Статистика по статусу транзакций",
            responses = @ApiResponse(responseCode = "200", description = "Количество по статусам",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    )
    public Map<String, Long> statusCount(
            @Parameter(description = "Период", example = "quarter")
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.statusCount(period);
    }

    @GetMapping("/transactions/by-bank")
    @Operation(summary = "Распределение транзакций по банкам",
            responses = @ApiResponse(responseCode = "200", description = "Агрегация по банкам",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    )
    public List<Map<String, Object>> byBank(
            @Parameter(description = "Роль: sender или receiver", example = "sender")
            @RequestParam(name = "role") String role,
            @Parameter(description = "Период", example = "week")
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.byBank(role, period);
    }

    @GetMapping("/transactions/by-category")
    @Operation(summary = "Статистика по категориям",
            responses = @ApiResponse(responseCode = "200", description = "Агрегация по категориям",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    )
    public List<Map<String, Object>> byCategory(
            @Parameter(description = "Тип транзакции: debit или credit", example = "debit")
            @RequestParam(name = "type") String type,
            @Parameter(description = "Период", example = "month")
            @RequestParam(name = "period", required = false, defaultValue = "week") String period
    ) {
        return dashboardService.byCategory(type, period);
    }
}