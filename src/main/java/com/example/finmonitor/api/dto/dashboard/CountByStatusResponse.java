package com.example.finmonitor.api.dto.dashboard;

import java.time.LocalDate;

/**
 * 4. Ответ: количество проведённых и отменённых транзакций
 */
public record CountByStatusResponse(
        LocalDate period_start,
        long completedCount,
        long cancelledCount
) {
}
