// File: src/main/java/com/example/finmonitor/domain/repository/DashboardRepository.java
package com.example.finmonitor.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository {

    List<Object[]> countGroupedByPeriod(String period, LocalDateTime from, LocalDateTime to);

    List<Object[]> countByTransactionType(LocalDateTime from, LocalDateTime to);

    Object[] sumIncomeAndExpense(LocalDateTime from, LocalDateTime to);

    List<Object[]> countByStatus(LocalDateTime from, LocalDateTime to);

    List<Object[]> statsBySenderBank(LocalDateTime from, LocalDateTime to);

    List<Object[]> statsByReceiverBank(LocalDateTime from, LocalDateTime to);

    List<Object[]> statsByCategory(LocalDateTime from, LocalDateTime to);
}
