package com.example.finmonitor.api;

import com.example.finmonitor.application.dto.dashboard.*;
import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "BearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "1. Динамика по количеству транзакций за заданный период")
    @GetMapping("/transactions/count")
    public ResponseEntity<List<CountByPeriodDto>> getCountByPeriod(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        return ResponseEntity.ok(dashboardService.countByPeriod(period));
    }

    @Operation(summary = "2. Распределение по типу транзакции (Поступление/Списание)")
    @GetMapping("/transactions/type")
    public ResponseEntity<List<TransactionTypeDto>> getByTransactionType(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        return ResponseEntity.ok(dashboardService.byTransactionType(period));
    }

    @Operation(summary = "3. Сравнение сумм поступлений и расходов за период")
    @GetMapping("/transactions/compare")
    public ResponseEntity<AmountComparisonDto> getAmountComparison(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        return ResponseEntity.ok(dashboardService.compareAmounts(period));
    }

    @Operation(summary = "4. Количество транзакций по статусам за период")
    @GetMapping("/transactions/status")
    public ResponseEntity<List<ExecutionStatusDto>> getByStatus(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        return ResponseEntity.ok(dashboardService.byStatus(period));
    }

    @Operation(summary = "5. Статистика по банкам отправителей или получателей")
    @GetMapping("/transactions/banks")
    public ResponseEntity<List<BankStatDto>> getByBank(
            @Parameter(description = "Роль банка (SENDER или RECEIVER)") @RequestParam @NotNull DashboardRole role,
            @Parameter(description = "Период агрегации")       @RequestParam @NotNull Period period
    ) {
        return ResponseEntity.ok(dashboardService.byBank(role, period));
    }

    @Operation(summary = "6. Статистика по категориям расходов/доходов")
    @GetMapping("/transactions/categories")
    public ResponseEntity<List<CategoryStatDto>> getByCategory(
            @Parameter(description = "Тип транзакции (CREDIT или DEBIT)") @RequestParam @NotNull TxnType type,
            @Parameter(description = "Период агрегации")                     @RequestParam @NotNull Period period
    ) {
        return ResponseEntity.ok(dashboardService.byCategory(type, period));
    }

}
