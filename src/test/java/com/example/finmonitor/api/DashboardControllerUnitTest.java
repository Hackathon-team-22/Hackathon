//package com.example.finmonitor.api;
//
//import com.example.finmonitor.api.dto.dashboard.AmountComparisonDto;
//import com.example.finmonitor.api.dto.dashboard.BankStatDto;
//import com.example.finmonitor.api.dto.dashboard.CategoryStatDto;
//import com.example.finmonitor.api.dto.dashboard.CountByPeriodDto;
//import com.example.finmonitor.api.dto.dashboard.ExecutionStatusDto;
//import com.example.finmonitor.api.dto.dashboard.TransactionTypeDto;
//import com.example.finmonitor.application.enums.DashboardRole;
//import com.example.finmonitor.application.enums.Period;
//import com.example.finmonitor.application.enums.TxnType;
//import com.example.finmonitor.application.usecase.dashboard.DashboardService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//public class DashboardControllerUnitTest {
//
//    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private DashboardService dashboardService;
//
//    @Mock
//    private UserContext userContext;
//
//    @InjectMocks
//    private DashboardController dashboardController;
//
//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
//        when(userContext.getCurrentUserId()).thenReturn(USER_ID);
//    }
//
//    @Test
//    public void testCountByPeriod() throws Exception {
//        CountByPeriodDto dto = new CountByPeriodDto(LocalDate.of(2025, 4, 1), 3L);
//        when(dashboardService.countByPeriod(USER_ID, Period.WEEK))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/dashboard/transactions/count")
//                        .param("period", Period.WEEK.name()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].periodStart").value("2025-04-01"))
//                .andExpect(jsonPath("$[0].totalCount").value(3));
//
//        verify(dashboardService).countByPeriod(USER_ID, Period.WEEK);
//    }
//
//    @Test
//    public void testByTransactionType() throws Exception {
//        TransactionTypeDto dto = new TransactionTypeDto(TxnType.CREDIT, 5L);
//        when(dashboardService.byTransactionType(USER_ID, Period.WEEK))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/dashboard/transactions/type")
//                        .param("period", Period.WEEK.name()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].type").value(TxnType.CREDIT.name()))
//                .andExpect(jsonPath("$[0].count").value(5));
//
//        verify(dashboardService).byTransactionType(USER_ID, Period.WEEK);
//    }
//
//    @Test
//    public void testCompareAmounts() throws Exception {
//        AmountComparisonDto dto = new AmountComparisonDto(new BigDecimal("100.50"), new BigDecimal("75.25"));
//        when(dashboardService.compareAmounts(USER_ID, Period.WEEK))
//                .thenReturn(dto);
//
//        mockMvc.perform(get("/dashboard/transactions/compare")
//                        .param("period", Period.WEEK.name()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.totalCredit").value(100.50))
//                .andExpect(jsonPath("$.totalDebit").value(75.25));
//
//        verify(dashboardService).compareAmounts(USER_ID, Period.WEEK);
//    }
//
//    @Test
//    public void testByStatus() throws Exception {
//        ExecutionStatusDto dto = new ExecutionStatusDto("Completed", 2L);
//        when(dashboardService.byStatus(USER_ID, Period.WEEK))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/dashboard/transactions/status")
//                        .param("period", Period.WEEK.name()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].status").value("Completed"))
//                .andExpect(jsonPath("$[0].count").value(2));
//
//        verify(dashboardService).byStatus(USER_ID, Period.WEEK);
//    }
//
//    @Test
//    public void testByBank() throws Exception {
//        UUID bankId = UUID.fromString("22222222-2222-2222-2222-222222222222");
//        BankStatDto dto = new BankStatDto(bankId, "MyBank", 4L, new BigDecimal("200.00"));
//        when(dashboardService.byBank(USER_ID, DashboardRole.SENDER, Period.WEEK))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/dashboard/transactions/banks")
//                        .param("role", DashboardRole.SENDER.name())
//                        .param("period", Period.WEEK.name()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].bankId").value(bankId.toString()))
//                .andExpect(jsonPath("$[0].bankName").value("MyBank"))
//                .andExpect(jsonPath("$[0].count").value(4))
//                .andExpect(jsonPath("$[0].sumAmount").value(200.00));
//
//        verify(dashboardService).byBank(USER_ID, DashboardRole.SENDER, Period.WEEK);
//    }
//
//    @Test
//    public void testByCategory() throws Exception {
//        UUID categoryId = UUID.fromString("33333333-3333-3333-3333-333333333333");
//        CategoryStatDto dto = new CategoryStatDto(categoryId, "Food", 7L, new BigDecimal("350.75"));
//        when(dashboardService.byCategory(USER_ID, TxnType.DEBIT, Period.WEEK))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/dashboard/transactions/categories")
//                        .param("type", TxnType.DEBIT.name())
//                        .param("period", Period.WEEK.name()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString()))
//                .andExpect(jsonPath("$[0].categoryName").value("Food"))
//                .andExpect(jsonPath("$[0].count").value(7))
//                .andExpect(jsonPath("$[0].sumAmount").value(350.75));
//
//        verify(dashboardService).byCategory(USER_ID, TxnType.DEBIT, Period.WEEK);
//    }
//
//    @Test
//    public void testInvalidEnumParameter() throws Exception {
//        mockMvc.perform(get("/dashboard/transactions/count")
//                        .param("period", "INVALID"))
//                .andExpect(status().isBadRequest());
//    }
//}
