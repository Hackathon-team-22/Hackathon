package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.dashboard.CountByPeriodRequest;
import com.example.finmonitor.api.dto.dashboard.CountByPeriodResponse;
import com.example.finmonitor.api.dto.dashboard.CountByTypeRequest;
import com.example.finmonitor.api.dto.dashboard.CountByTypeResponse;
import com.example.finmonitor.api.dto.dashboard.DynamicsByTypeRequest;
import com.example.finmonitor.api.dto.dashboard.CountByStatusRequest;
import com.example.finmonitor.api.dto.dashboard.CountByStatusResponse;
import com.example.finmonitor.api.mapper.DashboardMapper;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.application.usecase.dashboard.DashboardService;
import com.example.finmonitor.application.usecase.dashboard.model.query.CountByPeriodQuery;
import com.example.finmonitor.application.usecase.dashboard.model.query.CountByTypeQuery;
import com.example.finmonitor.application.usecase.dashboard.model.query.DynamicsByTypeQuery;
import com.example.finmonitor.application.usecase.dashboard.model.query.CountByStatusQuery;
import com.example.finmonitor.application.usecase.dashboard.model.result.CountByPeriodResult;
import com.example.finmonitor.application.usecase.dashboard.model.result.CountByTypeResult;
import com.example.finmonitor.application.usecase.dashboard.model.result.DynamicsByTypeResult;
import com.example.finmonitor.application.usecase.dashboard.model.result.CountByStatusResult;
import com.example.finmonitor.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private DashboardMapper mapper;

    @MockBean
    private UserContext userContext;

    @MockBean
    private JwtProvider jwtProvider;

    private final UUID userId = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @BeforeEach
    void setUp() {
        given(userContext.getCurrentUserId()).willReturn(userId);
    }

    @Nested
    @DisplayName("GET /dashboard/transactions/count")
    class GetCountByPeriod {

        @Test
        @DisplayName("возвращает 200 и JSON с количеством транзакций по периодам")
        void getCountByPeriod_returns200AndJson() throws Exception {
            CountByPeriodQuery query = new CountByPeriodQuery(
                    userId,
                    Period.MONTH,
                    LocalDateTime.of(2025, 4, 1, 0, 0),
                    LocalDateTime.of(2025, 4, 30, 23, 59)
            );
            CountByPeriodResult result = new CountByPeriodResult(
                    LocalDate.of(2025, 4, 1),
                    42L
            );
            CountByPeriodResponse apiResponse = new CountByPeriodResponse(
                    result.period_start(),
                    result.count()
            );

            given(mapper.toCountByPeriodQuery(any(CountByPeriodRequest.class), eq(userId)))
                    .willReturn(query);
            given(dashboardService.countByPeriod(eq(query)))
                    .willReturn(List.of(result));
            given(mapper.toCountByPeriodResponseList(anyList()))
                    .willReturn(List.of(apiResponse));

            mockMvc.perform(get("/dashboard/transactions/count")
                            .param("period", Period.MONTH.name())
                            .param("start",  "2025-04-01T00:00:00")
                            .param("end",    "2025-04-30T23:59:00")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].period_start").value("2025-04-01"))
                    .andExpect(jsonPath("$[0].count").value(42));
        }
    }

    @Nested
    @DisplayName("GET /dashboard/transactions/type")
    class GetCountByType {

        @Test
        @DisplayName("возвращает 200 и JSON с количеством транзакций по типам")
        void getCountByType_returns200AndJson() throws Exception {
            CountByTypeQuery query = new CountByTypeQuery(
                    userId,
                    TxnType.CREDIT,
                    Period.WEEK,
                    LocalDateTime.of(2025, 4, 30, 0, 0),
                    LocalDateTime.of(2025, 4, 30, 23, 59)
            );
            CountByTypeResult result = new CountByTypeResult(
                    LocalDate.of(2025, 4, 30),
                    7,
                    TxnType.CREDIT
            );
            CountByTypeResponse apiResponse = new CountByTypeResponse(
                    result.period_start(),
                    result.count(),
                    result.type()
            );

            given(mapper.toCountByTypeQuery(any(CountByTypeRequest.class), eq(userId)))
                    .willReturn(query);
            given(dashboardService.countByType(eq(query)))
                    .willReturn(List.of(result));
            given(mapper.toCountByTypeResponseList(anyList()))
                    .willReturn(List.of(apiResponse));

            mockMvc.perform(get("/dashboard/transactions/type")
                            .param("period", Period.WEEK.name())
                            .param("start",  "2025-04-30T00:00:00")
                            .param("end",    "2025-04-30T23:59:00")
                            .param("type",   TxnType.CREDIT.name())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].period_start").value("2025-04-30"))
                    .andExpect(jsonPath("$[0].count").value(7))
                    .andExpect(jsonPath("$[0].type").value("CREDIT"));
        }
    }

    @Nested
    @DisplayName("GET /dashboard/transactions/type-dynamics")
    class GetDynamicsByType {

        @Test
        @DisplayName("возвращает 200 и JSON с динамикой транзакций по типам")
        void getDynamicsByType_returns200AndJson() throws Exception {
            DynamicsByTypeQuery query = new DynamicsByTypeQuery(
                    userId,
                    TxnType.DEBIT,
                    Period.WEEK,
                    LocalDateTime.of(2025, 4, 30, 0, 0),
                    LocalDateTime.of(2025, 4, 30, 23, 59)
            );
            DynamicsByTypeResult result = new DynamicsByTypeResult(
                    LocalDate.of(2025, 4, 30),
                    3
            );
            CountByPeriodResponse apiResponse = new CountByPeriodResponse(
                    result.period_start(),
                    result.count()
            );

            given(mapper.toDynamicsByTypeQuery(any(DynamicsByTypeRequest.class), eq(userId)))
                    .willReturn(query);
            given(dashboardService.dynamicsByType(eq(query)))
                    .willReturn(List.of(result));
            given(mapper.toCountByPeriodResponseListFromDynamics(anyList()))
                    .willReturn(List.of(apiResponse));

            mockMvc.perform(get("/dashboard/transactions/type-dynamics")
                            .param("period", Period.WEEK.name())
                            .param("start",  "2025-04-30T00:00:00")
                            .param("end",    "2025-04-30T23:59:00")
                            .param("type",   TxnType.DEBIT.name())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].period_start").value("2025-04-30"))
                    .andExpect(jsonPath("$[0].count").value(3));
        }
    }

    @Nested
    @DisplayName("GET /dashboard/transactions/status")
    class GetCountByStatus {

        @Test
        @DisplayName("возвращает 200 и JSON с количеством завершенных и отмененных транзакций")
        void getCountByStatus_returns200AndJson() throws Exception {
            CountByStatusQuery query = new CountByStatusQuery(
                    userId,
                    Period.QUARTER,
                    LocalDateTime.of(2025, 1, 1, 0, 0),
                    LocalDateTime.of(2025, 3, 31, 23, 59)
            );
            CountByStatusResult result = new CountByStatusResult(
                    LocalDate.of(2025, 1, 1),
                    5,
                    2
            );
            CountByStatusResponse apiResponse = new CountByStatusResponse(
                    result.period_start(),
                    result.completedCount(),
                    result.cancelledCount()
            );

            given(mapper.toCountByStatusQuery(any(CountByStatusRequest.class), eq(userId)))
                    .willReturn(query);
            given(dashboardService.countByStatus(eq(query)))
                    .willReturn(List.of(result));
            given(mapper.toCountByStatusResponseList(anyList()))
                    .willReturn(List.of(apiResponse));

            mockMvc.perform(get("/dashboard/transactions/status")
                            .param("period", Period.QUARTER.name())
                            .param("start",  "2025-01-01T00:00:00")
                            .param("end",    "2025-03-31T23:59:00")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].period_start").value("2025-01-01"))
                    .andExpect(jsonPath("$[0].completedCount").value(5))
                    .andExpect(jsonPath("$[0].cancelledCount").value(2));
        }
    }
}
