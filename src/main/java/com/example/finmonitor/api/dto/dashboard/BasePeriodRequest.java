package com.example.finmonitor.api.dto.dashboard;

import com.example.finmonitor.application.enums.Period;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Общие поля: period, start, end
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Базовые параметры для всех запросов по периодам")
public abstract class BasePeriodRequest {
    @NotNull
    @Schema(description = "Шаг агрегации", example = "MONTH")
    private Period period;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "Начало интервала", example = "2025-01-01T00:00:00")
    private LocalDateTime start;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "Конец интервала", example = "2025-05-01T23:59:59")
    private LocalDateTime end;

    @AssertTrue(message = "start must be before or equal to end")
    private boolean isPeriodValid() {
        return start != null && end != null && !start.isAfter(end);
    }
}