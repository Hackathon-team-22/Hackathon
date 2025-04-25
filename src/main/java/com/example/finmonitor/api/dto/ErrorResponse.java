package com.example.finmonitor.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Формат стандартного ответа об ошибке")
public class ErrorResponse {

    @Schema(description = "Сообщение об ошибке", example = "Invalid username or password")
    private String error;

    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
