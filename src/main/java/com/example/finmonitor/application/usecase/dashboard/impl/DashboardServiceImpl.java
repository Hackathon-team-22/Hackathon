package com.example.finmonitor.application.usecase.dashboard.impl;

import com.example.finmonitor.application.usecase.dashboard.DashboardService;
import com.example.finmonitor.application.usecase.dashboard.model.query.*;
import com.example.finmonitor.application.usecase.dashboard.model.result.*;
import com.example.finmonitor.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository repository;

    public DashboardServiceImpl(DashboardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CountByPeriodResult> countByPeriod(CountByPeriodQuery query) {
        return repository.fetchCountByPeriod(query.userId(), query.period(), query.start(), query.end());
    }

    @Override
    public List<CountByTypeResult> countByType(CountByTypeQuery query) {
        return repository.fetchCountByType(query.userId(), query.type(), query.period(), query.start(), query.end());
    }

    @Override
    public List<DynamicsByTypeResult> dynamicsByType(DynamicsByTypeQuery query) {
        return repository.fetchDynamicsByType(query.userId(), query.type(), query.period(), query.start(), query.end());
    }

    @Override
    public List<CompareFundsResult> compareFunds(CompareFundsQuery query) {
        return repository.fetchCompareFunds(query.userId(), query.period(), query.start(), query.end());
    }

    @Override
    public List<CountByStatusResult> countByStatus(CountByStatusQuery query) {
        return repository.fetchCountByStatus(query.userId(), query.period(), query.start(), query.end());
    }

    @Override
    public List<StatsByBankResult> statsByBank(StatsByBankQuery query) {
        return repository.fetchStatsByBank(query.userId(), query.role(), query.period(), query.start(), query.end());
    }

    @Override
    public List<StatsByCategoryResult> statsByCategory(StatsByCategoryQuery query) {
        return repository.fetchStatsByCategory(query.userId(), query.type(), query.period(), query.start(), query.end());
    }
}
