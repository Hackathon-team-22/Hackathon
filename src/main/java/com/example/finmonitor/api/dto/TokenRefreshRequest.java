package com.example.finmonitor.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на обновление access токена")
public class TokenRefreshRequest {

    @Schema(description = "Refresh токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
