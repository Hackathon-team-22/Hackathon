package com.example.finmonitor.api.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryStatDto(
        UUID categoryId,
        String categoryName,
        long count,
        BigDecimal sumAmount
) {}
