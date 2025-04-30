package com.example.finmonitor.api.dto.dashboard;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.finmonitor.application.enums.TxnType;

/**
 * 2b. Динамика транзакций по типу
 */
@Getter
@Setter
public class DynamicsByTypeRequest extends BasePeriodRequest {
    @NotNull
    private TxnType type;
}