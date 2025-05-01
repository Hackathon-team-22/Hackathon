package com.example.finmonitor.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegisterRequest {

    @Schema(description = "Имя пользователя", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 3, max = 50)
    @NotBlank
    private String username;

    @Schema(description = "Пароль пользователя", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, max = 100)
    @NotBlank
    private String password;

}