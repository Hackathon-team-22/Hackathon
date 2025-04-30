package com.example.finmonitor.api.dto.dashboard;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.finmonitor.application.enums.TxnType;

/**
 * 6. Статистика по категориям расходов/доходов
 */
@Getter
@Setter
public class StatsByCategoryRequest extends BasePeriodRequest {
    @NotNull
    private TxnType type;
}
