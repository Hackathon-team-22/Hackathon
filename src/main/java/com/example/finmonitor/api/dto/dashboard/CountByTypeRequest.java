package com.example.finmonitor.api.dto.dashboard;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.finmonitor.application.enums.TxnType;

/**
 * 2. Распределение по типу транзакции
 */
@Getter
@Setter
public class CountByTypeRequest extends BasePeriodRequest {
    @NotNull
    private TxnType type;
}