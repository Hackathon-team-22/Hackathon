package com.example.finmonitor.api.dto.dashboard;

public record ExecutionStatusDto(
        String status,
        long count
) {}
