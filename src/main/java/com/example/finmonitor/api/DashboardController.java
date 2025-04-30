package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.dashboard.AmountComparisonDto;
import com.example.finmonitor.api.dto.dashboard.BankStatDto;
import com.example.finmonitor.api.dto.dashboard.CategoryStatDto;
import com.example.finmonitor.api.dto.dashboard.CountByPeriodDto;
import com.example.finmonitor.api.dto.dashboard.ExecutionStatusDto;
import com.example.finmonitor.api.dto.dashboard.TransactionTypeDto;
import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Метрики и статистика по транзакциям")
@SecurityRequirement(name = "BearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private UserContext userContext;

    @Operation(summary = "1. Динамика по количеству транзакций за заданный период",
            responses = @ApiResponse(responseCode = "200", description = "Список CountByPeriodDto",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CountByPeriodDto.class)))
    )
    @GetMapping("/transactions/count")
    public ResponseEntity<List<CountByPeriodDto>> getCountByPeriod(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        UUID userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.countByPeriod(userId, period));
    }

    @Operation(summary = "2. Распределение по типу транзакции (Поступление/Списание)",
            responses = @ApiResponse(responseCode = "200", description = "Список TransactionTypeDto",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransactionTypeDto.class)))
    )
    @GetMapping("/transactions/type")
    public ResponseEntity<List<TransactionTypeDto>> getByTransactionType(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        UUID userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.byTransactionType(userId, period));
    }

    @Operation(summary = "3. Сравнение сумм поступлений и расходов за период",
            responses = @ApiResponse(responseCode = "200", description = "AmountComparisonDto",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AmountComparisonDto.class)))
    )
    @GetMapping("/transactions/compare")
    public ResponseEntity<AmountComparisonDto> getAmountComparison(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        UUID userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.compareAmounts(userId, period));
    }

    @Operation(summary = "4. Количество транзакций по статусам за период",
            responses = @ApiResponse(responseCode = "200", description = "Список ExecutionStatusDto",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExecutionStatusDto.class)))
    )
    @GetMapping("/transactions/status")
    public ResponseEntity<List<ExecutionStatusDto>> getByStatus(
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        UUID userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.byStatus(userId, period));
    }

    @Operation(summary = "5. Статистика по банкам отправителей или получателей",
            responses = @ApiResponse(responseCode = "200", description = "Список BankStatDto",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BankStatDto.class)))
    )
    @GetMapping("/transactions/banks")
    public ResponseEntity<List<BankStatDto>> getByBank(
            @Parameter(description = "Роль банка (SENDER или RECEIVER)") @RequestParam @NotNull DashboardRole role,
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        UUID userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.byBank(userId, role, period));
    }

    @Operation(summary = "6. Статистика по категориям расходов/доходов",
            responses = @ApiResponse(responseCode = "200", description = "Список CategoryStatDto",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryStatDto.class)))
    )
    @GetMapping("/transactions/categories")
    public ResponseEntity<List<CategoryStatDto>> getByCategory(
            @Parameter(description = "Тип транзакции (CREDIT или DEBIT)") @RequestParam @NotNull TxnType type,
            @Parameter(description = "Период агрегации") @RequestParam @NotNull Period period
    ) {
        UUID userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.byCategory(userId, type, period));
    }

}
