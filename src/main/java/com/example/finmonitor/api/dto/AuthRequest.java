package com.example.finmonitor.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос авторизации пользователя")
public class AuthRequest {

    @Schema(description = "Имя пользователя", example = "user1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String username;

    @Schema(description = "Пароль пользователя", example = "secret123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
