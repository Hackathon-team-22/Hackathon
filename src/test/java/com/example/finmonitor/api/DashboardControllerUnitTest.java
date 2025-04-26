package com.example.finmonitor.api;

import com.example.finmonitor.application.dto.dashboard.*;
import com.example.finmonitor.application.enums.DashboardRole;
import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import com.example.finmonitor.application.service.DashboardService;
import com.example.finmonitor.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = DashboardController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)  // гарантированно отключаем все фильтры
public class DashboardControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    public void testGetCountByPeriod() throws Exception {
        List<CountByPeriodDto> data = List.of(
                new CountByPeriodDto(LocalDate.of(2025, 4, 1), 10)
        );
        when(dashboardService.countByPeriod(Period.MONTH)).thenReturn(data);

        mockMvc.perform(get("/dashboard/transactions/count")
                        .param("period", "MONTH"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].periodStart").value("2025-04-01"))
                .andExpect(jsonPath("$[0].totalCount").value(10));

        verify(dashboardService).countByPeriod(Period.MONTH);
    }

    @Test
    public void testGetByTransactionType() throws Exception {
        List<TransactionTypeDto> data = List.of(
                new TransactionTypeDto(TxnType.CREDIT, 5)
        );
        when(dashboardService.byTransactionType(Period.WEEK)).thenReturn(data);

        mockMvc.perform(get("/dashboard/transactions/type")
                        .param("period", "WEEK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CREDIT"))
                .andExpect(jsonPath("$[0].count").value(5));

        verify(dashboardService).byTransactionType(Period.WEEK);
    }

    @Test
    public void testGetAmountComparison() throws Exception {
        AmountComparisonDto dto = new AmountComparisonDto(
                new BigDecimal("1000.50"),
                new BigDecimal("750.25")
        );
        when(dashboardService.compareAmounts(Period.QUARTER)).thenReturn(dto);

        mockMvc.perform(get("/dashboard/transactions/compare")
                        .param("period", "QUARTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCredit").value(1000.50))
                .andExpect(jsonPath("$.totalDebit").value(750.25));

        verify(dashboardService).compareAmounts(Period.QUARTER);
    }

    @Test
    public void testGetByStatus() throws Exception {
        List<ExecutionStatusDto> data = List.of(
                new ExecutionStatusDto("Платеж выполнен", 8),
                new ExecutionStatusDto("Отменена", 2)
        );
        when(dashboardService.byStatus(Period.YEAR)).thenReturn(data);

        mockMvc.perform(get("/dashboard/transactions/status")
                        .param("period", "YEAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Платеж выполнен"))
                .andExpect(jsonPath("$[0].count").value(8))
                .andExpect(jsonPath("$[1].status").value("Отменена"))
                .andExpect(jsonPath("$[1].count").value(2));

        verify(dashboardService).byStatus(Period.YEAR);
    }

    @Test
    public void testGetByBank() throws Exception {
        List<BankStatDto> data = List.of(
                new BankStatDto(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "Bank A", 4, new BigDecimal("2000.00"))
        );
        when(dashboardService.byBank(eq(DashboardRole.SENDER), eq(Period.MONTH))).thenReturn(data);

        mockMvc.perform(get("/dashboard/transactions/banks")
                        .param("role", "SENDER")
                        .param("period", "MONTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bankId").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$[0].bankName").value("Bank A"))
                .andExpect(jsonPath("$[0].count").value(4))
                .andExpect(jsonPath("$[0].sumAmount").value(2000.00));

        verify(dashboardService).byBank(DashboardRole.SENDER, Period.MONTH);
    }

    @Test
    public void testGetByCategory() throws Exception {
        List<CategoryStatDto> data = List.of(
                new CategoryStatDto(
                        UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        "Food", 7, new BigDecimal("350.75"))
        );
        when(dashboardService.byCategory(eq(TxnType.DEBIT), eq(Period.WEEK))).thenReturn(data);

        mockMvc.perform(get("/dashboard/transactions/categories")
                        .param("type", "DEBIT")
                        .param("period", "WEEK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value("22222222-2222-2222-2222-222222222222"))
                .andExpect(jsonPath("$[0].categoryName").value("Food"))
                .andExpect(jsonPath("$[0].count").value(7))
                .andExpect(jsonPath("$[0].sumAmount").value(350.75));

        verify(dashboardService).byCategory(TxnType.DEBIT, Period.WEEK);
    }

    @Test
    public void testInvalidEnumParameter() throws Exception {
        mockMvc.perform(get("/dashboard/transactions/count")
                        .param("period", "INVALID"))
                .andExpect(status().isBadRequest());
    }
}
