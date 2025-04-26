package com.example.finmonitor.application.dto.dashboard;

import java.time.LocalDate;

public record CountByPeriodDto(
        LocalDate periodStart,
        long totalCount
) {}
