package com.example.finmonitor.application.usecase.dashboard;

import com.example.finmonitor.application.usecase.dashboard.model.query.*;
import com.example.finmonitor.application.usecase.dashboard.model.result.*;

import java.util.List;

public interface DashboardService {

    /** 1. Динамика по количеству транзакций за период */
    List<CountByPeriodResult> countByPeriod(CountByPeriodQuery query);

    /** 2. Распределение по типу транзакции за период */
    List<CountByTypeResult> countByType(CountByTypeQuery query);

    /** 2b. Динамика транзакций по типу и периоду */
    List<DynamicsByTypeResult> dynamicsByType(DynamicsByTypeQuery query);

    /** 3. Сравнение сумм поступлений и расходов за период */
    List<CompareFundsResult> compareFunds(CompareFundsQuery query);

    /** 4. Количество проведённых и отменённых транзакций за период */
    List<CountByStatusResult> countByStatus(CountByStatusQuery query);

    /** 5. Статистика по банкам-отправителям/получателям за период */
    List<StatsByBankResult> statsByBank(StatsByBankQuery query);

    /** 6. Статистика по категориям расходов/доходов за период */
    List<StatsByCategoryResult> statsByCategory(StatsByCategoryQuery query);
}
