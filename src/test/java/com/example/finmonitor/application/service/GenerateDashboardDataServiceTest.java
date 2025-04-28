package com.example.finmonitor.application.service;

import com.example.finmonitor.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GenerateDashboardDataServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private GenerateDashboardDataService dashboardService;

    private LocalDate from, to;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        from = LocalDate.of(2025, 1, 1);
        to   = LocalDate.of(2025, 12, 31);
    }

    @Test
    void execute_returnsAggregates() {
        when(transactionRepository.countByTimestampBetween(from, to)).thenReturn(100L);
        when(transactionRepository.countByStatusNameAndTimestampBetween("Платеж выполнен", from, to)).thenReturn(60L);
        when(transactionRepository.countByStatusNameAndTimestampBetween("Отменена", from, to)).thenReturn(10L);

        Map<String, Object> result = dashboardService.execute(from, to);

        assertThat(result).containsEntry("totalTransactions", 100L);
        assertThat(result).containsEntry("completedTransactions", 60L);
        assertThat(result).containsEntry("cancelledTransactions", 10L);

        verify(transactionRepository).countByTimestampBetween(from, to);
        verify(transactionRepository).countByStatusNameAndTimestampBetween("Платеж выполнен", from, to);
        verify(transactionRepository).countByStatusNameAndTimestampBetween("Отменена", from, to);
    }
}