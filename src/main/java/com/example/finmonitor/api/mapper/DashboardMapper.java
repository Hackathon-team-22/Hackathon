package com.example.finmonitor.api.mapper;

import com.example.finmonitor.api.dto.dashboard.*;
import com.example.finmonitor.application.usecase.dashboard.model.query.*;
import com.example.finmonitor.application.usecase.dashboard.model.result.*;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    // ================================
    // API-DTO Request → Use-Case Query
    // ================================

    CountByPeriodQuery toCountByPeriodQuery(
            CountByPeriodRequest request,
            UUID userId
    );

    CountByTypeQuery toCountByTypeQuery(
            CountByTypeRequest request,
            UUID userId
    );

    DynamicsByTypeQuery toDynamicsByTypeQuery(
            DynamicsByTypeRequest request,
            UUID userId
    );

    CompareFundsQuery toCompareFundsQuery(
            CompareFundsRequest request,
            UUID userId
    );

    CountByStatusQuery toCountByStatusQuery(
            CountByStatusRequest request,
            UUID userId
    );

    StatsByBankQuery toStatsByBankQuery(
            StatsByBankRequest request,
            UUID userId
    );

    StatsByCategoryQuery toStatsByCategoryQuery(
            StatsByCategoryRequest request,
            UUID userId
    );


    // =================================
    // Use-Case Result → API-DTO Response
    // =================================

    CountByPeriodResponse toCountByPeriodResponse(CountByPeriodResult model);
    List<CountByPeriodResponse> toCountByPeriodResponseList(List<CountByPeriodResult> models);

    CountByTypeResponse toCountByTypeResponse(CountByTypeResult model);
    List<CountByTypeResponse> toCountByTypeResponseList(List<CountByTypeResult> models);

    CountByPeriodResponse toCountByPeriodResponseFromDynamics(DynamicsByTypeResult model);
    List<CountByPeriodResponse> toCountByPeriodResponseListFromDynamics(List<DynamicsByTypeResult> models);

    CompareFundsResponse toCompareFundsResponse(CompareFundsResult model);
    List<CompareFundsResponse> toCompareFundsResponseList(List<CompareFundsResult> model);

    CountByStatusResponse toCountByStatusResponse(CountByStatusResult model);
    List<CountByStatusResponse> toCountByStatusResponseList(List<CountByStatusResult> model);

    StatsByBankResponse toStatsByBankResponse(StatsByBankResult model);
    List<StatsByBankResponse> toStatsByBankResponseList(List<StatsByBankResult> models);

    StatsByCategoryResponse toStatsByCategoryResponse(StatsByCategoryResult model);
    List<StatsByCategoryResponse> toStatsByCategoryResponseList(List<StatsByCategoryResult> models);

}
