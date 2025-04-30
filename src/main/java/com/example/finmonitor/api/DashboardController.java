package com.example.finmonitor.api.controller;

import com.example.finmonitor.api.UserContext;
import com.example.finmonitor.api.dto.dashboard.*;
import com.example.finmonitor.api.mapper.DashboardMapper;
import com.example.finmonitor.application.usecase.dashboard.DashboardService;
import com.example.finmonitor.application.usecase.dashboard.model.query.*;
import com.example.finmonitor.application.usecase.dashboard.model.result.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "BearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardMapper mapper;
    private final UserContext userContext;

    public DashboardController(DashboardService dashboardService,
                               DashboardMapper mapper,
                               UserContext userContext) {
        this.dashboardService = dashboardService;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    /** 1. Динамика по количеству транзакций за период */
    @GetMapping("/transactions/count")
    public ResponseEntity<List<CountByPeriodResponse>> getCountByPeriod(
            @Valid @ModelAttribute CountByPeriodRequest request) {
        UUID userId = userContext.getCurrentUserId();
        CountByPeriodQuery query = mapper.toCountByPeriodQuery(request, userId);
        List<CountByPeriodResult> results = dashboardService.countByPeriod(query);
        return ResponseEntity.ok(mapper.toCountByPeriodResponseList(results));
    }

    /** 2. Распределение по типу транзакции */
    @GetMapping("/transactions/type")
    public ResponseEntity<List<CountByTypeResponse>> getCountByType(
            @Valid @ModelAttribute CountByTypeRequest request) {
        UUID userId = userContext.getCurrentUserId();
        CountByTypeQuery query = mapper.toCountByTypeQuery(request, userId);
        List<CountByTypeResult> results = dashboardService.countByType(query);
        return ResponseEntity.ok(mapper.toCountByTypeResponseList(results));
    }

    /** 2b. Динамика транзакций по типу */
    @GetMapping("/transactions/type-dynamics")
    public ResponseEntity<List<CountByPeriodResponse>> getDynamicsByType(
            @Valid @ModelAttribute DynamicsByTypeRequest request) {
        UUID userId = userContext.getCurrentUserId();
        DynamicsByTypeQuery query = mapper.toDynamicsByTypeQuery(request, userId);
        List<DynamicsByTypeResult> results = dashboardService.dynamicsByType(query);
        return ResponseEntity.ok(mapper.toCountByPeriodResponseListFromDynamics(results));
    }

    /** 3. Сравнение сумм поступлений и расходов */
    @GetMapping("/transactions/compare")
    public ResponseEntity<List<CompareFundsResponse>> compareFunds(
            @Valid @ModelAttribute CompareFundsRequest request) {
        UUID userId = userContext.getCurrentUserId();
        CompareFundsQuery query = mapper.toCompareFundsQuery(request, userId);
        List<CompareFundsResult> result = dashboardService.compareFunds(query);
        return ResponseEntity.ok(mapper.toCompareFundsResponseList(result));
    }

    /** 4. Количество проведённых и отменённых транзакций */
    @GetMapping("/transactions/status")
    public ResponseEntity<List<CountByStatusResponse>> getCountByStatus(
            @Valid @ModelAttribute CountByStatusRequest request) {
        UUID userId = userContext.getCurrentUserId();
        CountByStatusQuery query = mapper.toCountByStatusQuery(request, userId);
        List<CountByStatusResult> result = dashboardService.countByStatus(query);
        return ResponseEntity.ok(mapper.toCountByStatusResponseList(result));
    }

    /** 5. Статистика по банкам отправителя/получателя */
    @GetMapping("/transactions/banks")
    public ResponseEntity<List<StatsByBankResponse>> getStatsByBank(
            @Valid @ModelAttribute StatsByBankRequest request) {
        UUID userId = userContext.getCurrentUserId();
        StatsByBankQuery query = mapper.toStatsByBankQuery(request, userId);
        List<StatsByBankResult> results = dashboardService.statsByBank(query);
        return ResponseEntity.ok(mapper.toStatsByBankResponseList(results));
    }

    /** 6. Статистика по категориям расходов/доходов */
    @GetMapping("/transactions/categories")
    public ResponseEntity<List<StatsByCategoryResponse>> getStatsByCategory(
            @Valid @ModelAttribute StatsByCategoryRequest request) {
        UUID userId = userContext.getCurrentUserId();
        StatsByCategoryQuery query = mapper.toStatsByCategoryQuery(request, userId);
        List<StatsByCategoryResult> results = dashboardService.statsByCategory(query);
        return ResponseEntity.ok(mapper.toStatsByCategoryResponseList(results));
    }
}
