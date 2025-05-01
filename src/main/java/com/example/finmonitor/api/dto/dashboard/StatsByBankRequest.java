package com.example.finmonitor.api.dto.dashboard;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.finmonitor.application.enums.DashboardRole;

/**
 * 5. Статистика по банкам отправителя/получателя
 */
@Getter
@Setter
public class StatsByBankRequest extends BasePeriodRequest {
    @NotNull
    private DashboardRole role;
}
