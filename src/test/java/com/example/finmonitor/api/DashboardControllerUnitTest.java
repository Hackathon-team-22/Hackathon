// File: src/test/java/com/example/finmonitor/api/DashboardControllerUnitTest.java
package com.example.finmonitor.api;

import com.example.finmonitor.application.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DashboardControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private com.example.finmonitor.security.JwtFilter jwtFilter;

    @MockitoBean
    private com.example.finmonitor.security.JwtProvider jwtProvider;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    void countByPeriod() throws Exception {
        given(dashboardService.countByPeriod("2025-Q1"))
                .willReturn(List.of(Map.of("period", "2025-Q1", "count", 5)));

        mockMvc.perform(get("/dashboard/transactions/count")
                        .param("period", "2025-Q1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].count").value(5));
    }

    @Test
    void byCategory() throws Exception {
        given(dashboardService.byCategory("expense", "2025-Q1"))
                .willReturn(List.of(Map.of("category", "Food", "amount", 200)));

        mockMvc.perform(get("/dashboard/transactions/by-category")
                        .param("type", "expense")
                        .param("period", "2025-Q1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(200));
    }

    @Test
    void byBank() throws Exception {
        given(dashboardService.byBank("USER", "2025-Q1"))
                .willReturn(List.of(Map.of("bank", "Bank A", "sum", 1000)));

        mockMvc.perform(get("/dashboard/transactions/by-bank")
                        .param("role", "USER")
                        .param("period", "2025-Q1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sum").value(1000));
    }
}
