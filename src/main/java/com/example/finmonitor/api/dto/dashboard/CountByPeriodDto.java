package com.example.finmonitor.api.dto.dashboard;

import java.time.LocalDate;

public record CountByPeriodDto(
        LocalDate periodStart,
        long totalCount
) {}
